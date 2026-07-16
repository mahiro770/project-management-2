package com.github.mahiro.projectmanager;

import java.sql.Timestamp;
import java.util.List;

public class ProjectService {

    private final ProjectRepository repo = new ProjectRepository();

    // 案件一覧取得
    public List<Project> getProjectList() {
        return repo.findAll();
    }

    // 案件詳細取得
    public Project getProjectDetail(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("案件番号は1以上を入力してください。");
        }

        Project project = repo.findById(id);

        if (project == null) {
            throw new ProjectNotFoundException(id);
        }

        return project;
    }

    // 案件登録（登録後の案件を返す）
    public Project registerProject(String title,
                                String clientName,
                                String skills,
                                String location,
                                String min,
                                String max,
                                String category) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("タイトルは必須です。");
        }

        if (clientName == null || clientName.isBlank()) {
            throw new IllegalArgumentException("会社名は必須です。");
        }

        Integer priceMin = (min == null || min.isBlank()) ? null : Integer.parseInt(min);
        Integer priceMax = (max == null || max.isBlank()) ? null : Integer.parseInt(max);
        if( priceMin != null && priceMax != null && priceMax < priceMin){
            throw new IllegalArgumentException("上限は下限以上の値を入力してください");
        }

        Project project = new Project(
                null,
                title,
                clientName,
                skills,
                location,
                priceMin,
                priceMax,
                "OPEN",
                null,
                null,
                false,
                "develop");

    
        Integer generatedId = repo.insert(project);

        if (generatedId == null) {
            throw new IllegalStateException("案件登録に失敗しました。");
        }

        return repo.findById(generatedId);
    }

    // 案件更新（更新後の案件を返す）
    public Project updateProject(int id,
                              String newTitle,
                              String newClientName,
                              String newSkills,
                              String newLocation,
                              String inputPriceMin,
                              String inputPriceMax,
                              String newStatus,
                              String newCategory) {

        Project project = repo.findById(id);

        if (project == null) {
            throw new ProjectNotFoundException(id);
        }

        boolean updateTitle = !newTitle.isBlank();
        if (updateTitle) {
            project.setTitle(newTitle);
        }

        boolean updateClientName = !newClientName.isBlank();
        if (updateClientName) {
            project.setClientName(newClientName);
        }

        boolean updateSkills = !newSkills.isBlank();
        if (updateSkills) {
            project.setRequiredSkills(newSkills);
        }

        boolean updateLocation = !newLocation.isBlank();
        if (updateLocation) {
            project.setLocation(newLocation);
        }

        boolean updatePriceMin = !inputPriceMin.isBlank();
        if (updatePriceMin) {
            project.setPriceMin(Integer.parseInt(inputPriceMin));
        }

        boolean updatePriceMax = !inputPriceMax.isBlank();
        if (updatePriceMax) {
            project.setPriceMax(Integer.parseInt(inputPriceMax));
        }

        if (project.getPriceMin() != null && project.getPriceMax() != null
             && project.getPriceMax() < project.getPriceMin()) {
                throw new IllegalArgumentException("上限額は下限額以上の値を入力してください");
             }

        boolean updateStatus = !newStatus.isBlank();
        if (updateStatus) {
            project.setStatus(newStatus);
        }

        boolean updateCategory = !newCategory.isBlank();
        if (updateCategory) {
            project.setCategory(newCategory);
        }
        
        project.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        repo.update(
                project,
                updateTitle,
                updateClientName,
                updateSkills,
                updateLocation,
                updatePriceMin,
                updatePriceMax,
                updateStatus,
                updateCategory);

        return repo.findById(id);
    }

    // 応募済み状態の更新（更新後の案件を返す）
    public Project setApplied(int id, boolean applied) {

        Project project = repo.findById(id);

        if (project == null) {
            throw new ProjectNotFoundException(id);
        }

        if (!repo.updateApplied(id, applied)) {
            throw new IllegalStateException("応募状況の更新に失敗しました。");
        }

        return repo.findById(id);
    }

    // カテゴリの更新（更新後の案件を返す）
    public Project setCategory(int id, String category) {

        Project project = repo.findById(id);

        if (project == null) {
            throw new ProjectNotFoundException(id);
        }

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("カテゴリは必須です。");
        }

        if (!repo.updateCategory(id, category)) {
            throw new IllegalStateException("カテゴリの更新に失敗しました。");
        }

        return repo.findById(id);
    }

    // 案件削除
    public void deleteProject(int id) {

        Project project = repo.findById(id);

        if (project == null) {
            throw new ProjectNotFoundException(id);
        }

        if (!repo.delete(id)) {
            throw new IllegalStateException("削除に失敗しました。");
        }
    }
}
