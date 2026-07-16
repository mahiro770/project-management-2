package com.github.mahiro.projectmanager;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in, "UTF-8");
        ProjectService service = new ProjectService();

        try (Connection conn = Database.getConnection()) {
            if (conn != null) {
                System.out.println("データベース連携成功");
            }

            while (true) {
                while (true) {
                    System.out.println("案件配信ツールです。このまま操作を続けますか？（Y/N)");
                    System.out.print("(Y/N):");
                    String judgeString = scan.nextLine();

                    if (judgeString.equalsIgnoreCase("Y")) {
                        break;
                    } else if (judgeString.equalsIgnoreCase("N")) {
                        return;
                    } else {
                        System.out.println("不正な入力です。再度入力してください");
                    }

                }

                System.out.println("操作を以下より選んでください(指定の番号えらんでください)");
                System.out.println("\n==========操作方法============");
                System.out.println("1:案件一覧表示");
                System.out.println("2:案件詳細表示");
                System.out.println("3:案件登録");
                System.out.println("4:案件更新");
                System.out.println("5:案件削除");
                System.out.println("\n==============================");
                System.out.print("操作番号:");

                int number = scan.nextInt();
                scan.nextLine();

                switch (number) {

                    // 案件一覧表示
                    case 1:
                        // サービスを生成
                        while (true) {
                            System.out.print("一覧表示に進みますか(Y/N)？:");
                            String st = scan.nextLine();

                            if (st.equalsIgnoreCase("Y")) {
                                System.out.println("\n==========案件一覧===========");
                                List<Project> list = service.getProjectList(); // ロジックはServiceへ

                                for (Project p : list) {
                                    System.out.println("\n" + p.toString());
                                }
                                System.out.println("\n============================\n");
                                break;

                            } else if (st.equalsIgnoreCase("N")) {
                                System.out.println("一覧表示をキャンセルします");
                                break;
                            }
                        }
                        break;

                    // 案件詳細表示
                    case 2:
                        while (true) {
                            // 一覧表示は以前作った Service メソッドを活用
                            List<Project> lists = service.getProjectList();

                            for (Project p : lists) {
                                System.out.println("\n" + p);
                            }

                            System.out.println("\n詳細一覧表示を開始します（戻る場合は-1)");
                            System.out.print("詳細を表示したい案件番号を入力してください: ");

                            int selectId = scan.nextInt();
                            scan.nextLine();

                            if (selectId == -1) {
                                System.out.println("詳細表示をキャンセルします");
                                break;
                            }

                            try {
                                Project project = service.getProjectDetail(selectId);

                                System.out.println("\n==========案件詳細==========");

                                System.out.println("ID：" + project.getId());
                                System.out.println("案件名：" + project.getTitle());
                                System.out.println("会社名：" + project.getClientName());
                                System.out.println("必須スキル：" + project.getRequiredSkills());
                                System.out.println("勤務地：" + project.getLocation());
                                System.out.println("最低金額：" + project.getPriceMin());
                                System.out.println("最高金額：" + project.getPriceMax());
                                System.out.println("配属状況：" + project.getStatus());
                                System.out.println("カテゴリ:"  + project.getCategory());
                                System.out.println("作成日時：" + project.getCreatedAt());
                                System.out.println("更新日時：" + project.getUpdatedAt());

                                System.out.println("============================");
                            } catch (IllegalArgumentException | ProjectNotFoundException e) {
                                System.out.println("エラー: " + e.getMessage());
                            }
                        }
                        break;

                    // 案件登録

                    // case 3 の中身を以下のように書き換えます
                    case 3:
                        System.out.println("案件登録を開始します");
                        System.out.print("タイトル(必須):");
                        String title = scan.nextLine();
                        if (title.equalsIgnoreCase("R"))
                            break;

                        System.out.print("会社名(必須):");
                        String client = scan.nextLine();
                        System.out.print("スキル:");
                        String skill = scan.nextLine();
                        System.out.print("勤務地:");
                        String loc = scan.nextLine();
                        System.out.print("最低金額:");
                        String min = scan.nextLine();
                        System.out.print("最高金額:");
                        String max = scan.nextLine();
                        System.out.print("カテゴリ");
                        String category = scan.nextLine();

                        try {
                            // ここでServiceに判断を任せる

                            Project registered = service.registerProject(title, client, skill, loc, min, max, category);
                            System.out.println("案件を登録しました。(ID: " + registered.getId() + ")");
                        } catch (IllegalArgumentException e) {
                            System.out.println("エラー: " + e.getMessage());
                        } catch (Exception e) {
                            System.out.println("登録中に予期せぬエラーが発生しました。");
                        }
                        break;

                    // 案件更新
                    case 4:

                        while (true) {

                            System.out.println("更新処理を開始します");

                            List<Project> projects = service.getProjectList();

                            for (Project p : projects) {
                                System.out.println();
                                System.out.print(p.toString());
                                System.out.println();
                            }

                            System.out.print("更新したい番号を入力してください(戻る場合は、-1を入力)");
                            System.out.print("番号:");
                            int change_id = scan.nextInt();
                            scan.nextLine();

                            if (change_id == -1) {
                                System.out.println();
                                System.out.println("更新処理をキャンセルしました");
                                System.out.println();
                                break;
                            }
                            Project project;
                            try {
                                project = service.getProjectDetail(change_id);
                            } catch (IllegalArgumentException | ProjectNotFoundException e) {
                                System.out.println("エラー: " + e.getMessage());
                                break;
                            }
                            System.out.println("\n==========案件一覧===========");
                            System.out.println("ID：" + project.getId());
                            System.out.println("案件名：" + project.getTitle());
                            System.out.println("会社名：" + project.getClientName());
                            System.out.println("必須スキル：" + project.getRequiredSkills());
                            System.out.println("勤務地：" + project.getLocation());
                            System.out.println("最低金額：" + project.getPriceMin());
                            System.out.println("最高金額：" + project.getPriceMax());
                            System.out.println("配属状況：" + project.getStatus());
                            System.out.println("カテゴリ:" + project.getCategory());
                            System.out.println("作成日時：" + project.getCreatedAt());
                            System.out.println("更新日時：" + project.getUpdatedAt());
                            System.out.println("\n============================");
                            System.out.println();

                            System.out.print("案件名を変更してください（変更しない場合はEnter) : ");
                            String newTitle = scan.nextLine();

                            System.out.print("会社名を変更してください（変更しない場合はEnter）： ");
                            String newClientName = scan.nextLine();

                            System.out.print("必須スキルを変更してください（変更しない場合はEnter）： ");
                            String newSkills = scan.nextLine();

                            System.out.print("勤務地を変更してください（変更しない場合はEnter）： ");
                            String newLocation = scan.nextLine();

                            System.out.print("最低金額を変更してください（変更しない場合はEnter）： ");
                            String inputPriceMin = scan.nextLine();

                            System.out.print("最高金額を変更してください（変更しない場合はEnter）： ");
                            String inputPriceMax = scan.nextLine();

                            System.out.print("配属状況を変更してください（変更しない場合はEnter）： ");
                            String newStatus = scan.nextLine();

                            System.out.println("カテゴリを変更してください（変更しない場合はEnter) : ");
                            String newCategory = scan.nextLine();

                            try {
                                service.updateProject(change_id, newTitle, newClientName, newSkills,
                                        newLocation, inputPriceMin, inputPriceMax, newStatus, newCategory);
                                System.out.println("更新が完了しました。");
                            } catch (IllegalArgumentException | ProjectNotFoundException e) {
                                System.out.println("エラー: " + e.getMessage());
                            }
                            break;
                        }
                        break;

                    // 削除処理
                    case 5:
                        System.out.println("削除処理を開始します...");
                        System.out.print("削除する案件番号を入力してください(戻る場合は-1): ");
                        int delete_id = scan.nextInt();
                        scan.nextLine();

                        if (delete_id == -1)
                            break;

                        System.out.print("本当に案件" + delete_id + "を削除してもいいですか？（Y/N）: ");
                        String judge = scan.nextLine();

                        if ("Y".equalsIgnoreCase(judge)) {
                            try {
                                service.deleteProject(delete_id); // 処理をServiceに委譲
                                System.out.println("削除が完了しました。");
                            } catch (ProjectNotFoundException | IllegalStateException e) {
                                System.out.println("エラー: " + e.getMessage());
                            }
                        } else if ("N".equalsIgnoreCase(judge)) {
                            System.out.println("削除をキャンセルしました。");
                        } else {
                            System.out.println("無効な入力です。");
                        }
                        break;

                    default:
                        System.out.println("不正な入力です。再度にゅうりょくしてください");
                        break;

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("データベースにつながっていません");
        } finally{
            scan.close();
        }

    }

}
