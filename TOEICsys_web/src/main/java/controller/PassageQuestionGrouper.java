package controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import beans.PassageGroup;
import beans.PassageQuestionGroup;
import beans.Question;
import dao.PassageGroupDao;

final class PassageQuestionGrouper {
    private PassageQuestionGrouper() {
    }

    static List<PassageQuestionGroup> group(
            List<Question> questions,
            int partType,
            PassageGroupDao passageGroupDao
    ) throws SQLException {
        Map<Long, List<Question>> questionsByGroup =
                new LinkedHashMap<>();

        for (Question question : questions) {
            if (question.getPartType() != partType) {
                continue;
            }
            Long groupId = question.getGroupId();
            if (groupId == null) {
                throw new SQLException(
                        "Part " + partType + " の問題に本文グループがありません"
                );
            }
            questionsByGroup.computeIfAbsent(
                    groupId,
                    ignored -> new ArrayList<>()
            ).add(question);
        }

        List<PassageQuestionGroup> groups = new ArrayList<>();
        for (Map.Entry<Long, List<Question>> entry
                : questionsByGroup.entrySet()) {
            PassageGroup passage =
                    passageGroupDao.getGroupById(entry.getKey());
            if (passage == null) {
                throw new SQLException(
                        "本文グループが見つかりません: " + entry.getKey()
                );
            }
            groups.add(new PassageQuestionGroup(
                    passage,
                    entry.getValue()
            ));
        }
        return groups;
    }
}
