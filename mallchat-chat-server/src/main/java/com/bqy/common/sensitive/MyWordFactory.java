package com.bqy.common.sensitive;

import com.bqy.common.common.algorithm.sensitiveWord.IWordFactory;
import com.bqy.common.sensitive.dao.SensitiveWordDao;
import com.bqy.common.sensitive.domain.entity.SensitiveWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MyWordFactory implements IWordFactory {
    @Autowired
    private SensitiveWordDao sensitiveWordDao;

    @Override
    public List<String> getWordList() {
        return sensitiveWordDao.list()
                .stream()
                .map(SensitiveWord::getWord)
                .collect(Collectors.toList());
    }
}
