package com.github.mahiro.projectmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProjectRepository {

    // ResultSet → Project に変換する共通メソッド
    private Project mapProject(ResultSet rs) throws java.sql.SQLException {
        return new Project(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("client_name"),
                rs.getString("required_skill"),
                rs.getString("location"),
                rs.getInt("price_min"),
                rs.getInt("price_max"),
                rs.getString("status"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at"),
                rs.getBoolean("applied"),
                rs.getString("category"));
    }

    // 案件一覧取得
    public List<Project> findAll() {

        List<Project> projects = new ArrayList<>();

        String sql = "SELECT * FROM projects ORDER BY id";

        try (Connection conn = Database.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql);
                ResultSet rs = stm.executeQuery()) {

            while (rs.next()) {
                projects.add(mapProject(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("案件一覧の取得に失敗しました。");
        }

        return projects;
    }

    // ID検索
    public Project findById(int id) {

        String sql = "SELECT * FROM projects WHERE id = ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setInt(1, id);

            try (ResultSet rs = stm.executeQuery()) {

                if (rs.next()) {
                    return mapProject(rs);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("案件情報の取得に失敗しました。");
        }

        return null;
    }

    // 登録（生成されたIDを返す）
    public Integer insert(Project project) {

        String sql = """
                INSERT INTO projects
                (title, client_name, required_skill, location, price_min, price_max)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = Database.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            stm.setString(1, project.getTitle());
            stm.setString(2, project.getClientName());
            stm.setString(3, project.getRequiredSkills());
            stm.setString(4, project.getLocation());

            if (project.getPriceMin() != null) {
                stm.setInt(5, project.getPriceMin());
            } else {
                stm.setNull(5, java.sql.Types.INTEGER);
            }

            if (project.getPriceMax() != null) {
                stm.setInt(6, project.getPriceMax());
            } else {
                stm.setNull(6, java.sql.Types.INTEGER);
            }

            stm.executeUpdate();

            try (ResultSet keys = stm.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("案件登録に失敗しました。");
        }

        return null;
    }

    // 更新
    public void update(Project project,
            boolean updateTitle,
            boolean updateClientName,
            boolean updateRequiredSkill,
            boolean updateLocation,
            boolean updatePriceMin,
            boolean updatePriceMax,
            boolean updateStatus,
            boolean updateCategory) {

        List<String> setClauses = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        if (updateTitle) {
            setClauses.add("title = ?");
            values.add(project.getTitle());
        }

        if (updateClientName) {
            setClauses.add("client_name = ?");
            values.add(project.getClientName());
        }

        if (updateRequiredSkill) {
            setClauses.add("required_skill = ?");
            values.add(project.getRequiredSkills());
        }

        if (updateLocation) {
            setClauses.add("location = ?");
            values.add(project.getLocation());
        }

        if (updatePriceMin) {
            setClauses.add("price_min = ?");
            values.add(project.getPriceMin());
        }

        if (updatePriceMax) {
            setClauses.add("price_max = ?");
            values.add(project.getPriceMax());
        }

        if (updateStatus) {
            setClauses.add("status = ?");
            values.add(project.getStatus());
        }

        if (updateCategory) {
            setClauses.add("category = ?");
            values.add(project.getCategory());
        }

        setClauses.add("updated_at = ?");
        values.add(project.getUpdatedAt());

        String sql = "UPDATE projects SET "
                + String.join(", ", setClauses)
                + " WHERE id = ?";

        values.add(project.getId());

        try (Connection conn = Database.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.size(); i++) {
                stm.setObject(i + 1, values.get(i));
            }

            stm.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("更新に失敗しました。");
        }
    }

    // カテゴリの更新
    public boolean updateCategory(int id, String category) {

        String sql = "UPDATE projects SET category = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setString(1, category);
            stm.setInt(2, id);

            return stm.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("カテゴリの更新に失敗しました。");
        }

        return false;
    }

    // 応募済みフラグの更新（連動してstatusもOPEN/CLOSEDに切り替える）
    public boolean updateApplied(int id, boolean applied) {

        String sql = "UPDATE projects SET applied = ?, status = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setBoolean(1, applied);
            stm.setString(2, applied ? "CLOSED" : "OPEN");
            stm.setInt(3, id);

            return stm.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("応募状況の更新に失敗しました。");
        }

        return false;
    }

    // idで参照し、そのレコードをすべて削除
    public boolean delete(int id) {

        String sql = "DELETE FROM projects WHERE id = ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setInt(1, id);

            return stm.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
