package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import beans.PassageGroup;
import beans.PassageGroupItem;

public class PassageGroupDao {
    public PassageGroup getGroupById(long groupId) throws SQLException {
        String groupSql = """
                SELECT group_id,
                       part_type,
                       group_type,
                       title
                  FROM passage_groups
                 WHERE group_id = ?
                """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement groupPs = conn.prepareStatement(groupSql)) {
            groupPs.setLong(1, groupId);

            PassageGroup group;
            try (ResultSet groupRs = groupPs.executeQuery()) {
                if (!groupRs.next()) {
                    return null;
                }
                group = new PassageGroup(
                        groupRs.getLong("group_id"),
                        groupRs.getInt("part_type"),
                        groupRs.getString("group_type"),
                        groupRs.getString("title"),
                        new ArrayList<>()
                );
            }

            String itemSql = """
                    SELECT item_id,
                           group_id,
                           sequence_no,
                           speaker,
                           text_body
                      FROM passage_group_items
                     WHERE group_id = ?
                  ORDER BY sequence_no
                    """;
            try (PreparedStatement itemPs = conn.prepareStatement(itemSql)) {
                itemPs.setLong(1, groupId);
                try (ResultSet itemRs = itemPs.executeQuery()) {
                    while (itemRs.next()) {
                        group.getItems().add(new PassageGroupItem(
                                itemRs.getLong("item_id"),
                                itemRs.getLong("group_id"),
                                itemRs.getInt("sequence_no"),
                                itemRs.getString("speaker"),
                                itemRs.getString("text_body")
                        ));
                    }
                }
            }
            return group;
        }
    }
}