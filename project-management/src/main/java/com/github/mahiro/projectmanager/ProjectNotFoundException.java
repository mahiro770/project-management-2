package com.github.mahiro.projectmanager;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(int id) {
        super("案件番号 " + id + " は存在しません。");
    }
}
