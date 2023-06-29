package com.MoneyPlant.service;

import com.MoneyPlant.dto.CategoryDto;
import com.MoneyPlant.repository.CardRepository;
import com.MoneyPlant.repository.ExpenseRepository;
import com.MoneyPlant.service.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class CardService {

    private boolean isFirstExecution = true;
    private final CardRepository cardRepository;
    private final ExpenseRepository expenseRepository;


    @PostConstruct
    public void executeCardCrawlerOnStartup() {
        if (isFirstExecution) {
            executeCardCrawler();
            isFirstExecution = false;
        }
    }

    @Scheduled(cron = "0 0 4 * * ?", zone = "Asia/Seoul") // 매일 새벽 4시에 실행, 1분마다 실행은 cron = "0 */1 * * * ?"
    public void executeCardCrawlerScheduled() {
        executeCardCrawler();
    }

    @Transactional
    private void executeCardCrawler() {
        try {
            cardRepository.deleteAll(); // card_list의 모든 데이터를 삭제합니다.
            String pythonScriptPath = "src/main/resources/python/CardCrolling.py";
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("CardCrolling.py 실행이 성공했습니다.");
                cardRepository.deleteByCardName("신용카드");
                cardRepository.deleteByCardName("이벤트카드");
            } else {
                log.error("CardCrolling.py 실행이 실패했습니다. 종료 코드: " + exitCode);
                executeCardCrawler();
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> manyExpenseTop3Category(UserDetailsImpl userDetails) {
        Long userId = userDetails.getId();
        List<Map<?, ?>> result = expenseRepository.findTop3CategoriesByUserAndCurrentMonth(userId);
        List<String> categoryNameList = new ArrayList<>();

        for (Map<?, ?> map : result) {
            String categoryName = (String) map.get("categoryName");
            categoryNameList.add(categoryName);
        }
        List<String> findCardList = findDuplicateCardNamesByCategories(categoryNameList);
        return findCardList;
    }


    public List<String> findDuplicateCardNamesByCategories(List<String> categoryNames) {
        List<Object[]> duplicateCards = cardRepository.findDuplicateCardNamesByCategories(categoryNames);
        List<String> duplicateCardNames = new ArrayList<>();

        for (Object[] result : duplicateCards) {
            String cardName = (String) result[0];
            duplicateCardNames.add(cardName);
        }

        return duplicateCardNames;
    }
}
