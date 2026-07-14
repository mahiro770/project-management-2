// TODO: projectApi.js から一覧を取得し、#project-list に描画する
// 状態表示は #status-message（読み込み中 / 0件 / エラー）を使う

import { getProjectList, updateApplied, deleteProject } from "./api/projectApi.js";

//都道府県名から東日本/中日本/西日本を判定するための簡易マップ
const REGION_KEYWORDS = {
  east: [
    "北海道",
    "青森",
    "岩手",
    "宮城",
    "秋田",
    "山形",
    "福島",
    "茨城",
    "栃木",
    "群馬",
    "埼玉",
    "千葉",
    "東京",
    "神奈川",
    "新潟",
    "山梨",
    "長野",
  ],
  central: ["富山", "石川", "福井", "岐阜", "静岡", "愛知", "三重"],
  west: [
    "滋賀",
    "京都",
    "大阪",
    "兵庫",
    "奈良",
    "和歌山",
    "鳥取",
    "島根",
    "岡山",
    "広島",
    "山口",
    "徳島",
    "香川",
    "愛媛",
    "高知",
    "福岡",
    "佐賀",
    "長崎",
    "熊本",
    "大分",
    "宮崎",
    "鹿児島",
    "沖縄",
  ],
};

const CATEGORY_LABELS = {
  develop: "開発",
  embedded: "組み込み",
  infra: "インフラ",
};

let currentRegion = "all";
function matchesRegion(location, region) {
  if (region === "all" || !region) {
    return true;
  }

  const keywords = REGION_KEYWORDS[region] || [];
  return keywords.some((keyword) => location && location.includes(keyword));
}

//カテゴリ検索
const currentCategories = new Set();

function matchesCategory(category, selected) {
  if (selected.size === 0) {
    return true;
  }

  return selected.has(category);
}

//お気に入り登録
const FAVORITE_STORAGE_KEY = "favoriteProjectIds";

function getFavoriteIds() {
  const raw = localStorage.getItem(FAVORITE_STORAGE_KEY);
  return raw ? JSON.parse(raw) : [];
}

function isFavorite(id) {
  return getFavoriteIds().includes(id);
}

function toggleFavorite(id) {
  const ids = getFavoriteIds();
  const index = ids.indexOf(id);

  if (index === -1) {
    ids.push(id);
  } else {
    ids.splice(index, 1);
  }

  localStorage.setItem(FAVORITE_STORAGE_KEY, JSON.stringify(ids));
  return ids.includes(id); //追加された場合はtrue,削除された場合はfalse
}

function isFavoriteViewRequested() {
  const params = new URLSearchParams(window.location.search);
  return params.get("favorite") === "true";
}



//キーワード検索と地域タブを両方まとめて適用する
function applyFilters() {
  const filtered = filterProjects(keywordInput.value)
    .filter((project) => matchesRegion(project.location, currentRegion))
    .filter((project) => matchesCategory(project.category, currentCategories));
  renderList(filtered);
}

const listEl = document.getElementById("project-list");
const statusEl = document.getElementById("status-message");
const keywordInput = document.getElementById("keyword-input");
const resultCountEl = document.getElementById("result-count");

let allProjects = []; //APIから取得した全データをここに保存しておく

function renderStatus(message) {
  statusEl.textContent = message;
  statusEl.hidden = message === "";
}

function renderList(projects) {
  listEl.innerHTML = "";
  resultCountEl.textContent = `該当案件数: ${projects.length}件`;

  if (projects.length === 0) {
    renderStatus("該当の案件が見つかりませんでした。");
    return;
  }

  renderStatus("");

  for (const project of projects) {
    const card = document.createElement("article");
    card.className = "project-card";
    card.innerHTML = `
        　　<button class="favorite-btn ${isFavorite(project.id) ? "active" : ""}">
                ${isFavorite(project.id) ? "★" : "☆"}
            </button>
           
            <h2>${project.title}</h2>

            <div class="project-info">
             <span class="label">会社名:</span> 
             <span>${project.clientName}</span>
             <span class="label">案件名:</span> 
             <span>${project.title}</span>
             <span class="label">スキル:</span> 
             <span>${project.requiredSkills}</span>
             <span class="label">勤務地:</span> 
             <span>${project.location}</span>
             <span class="label">単価:</span>
             <span>${project.priceMin}円 ~ ${project.priceMax}円</span>
             <span class="label">カテゴリ:</span>
             <span>${CATEGORY_LABELS[project.category] ?? project.category}</span>
             <button class="applied-btn ${project.applied ? "done" : ""}" >
                ${project.applied ? "応募済み" : "応募する"}
             </button>
            </div>
            <a class="detail-link" href="detail?id=${project.id}">詳細を見る</a>
            <button class="delete-btn">削除</button>
            <div class="delete-confirm" hidden>
              <p>この案件を削除しますか？</p>
              <p>削除後は復元できません。</p>
              <button class="confirm-delete-btn">削除</button>
              <button class="cancel-delete-btn">キャンセル</button>
            </div>
        `;
    listEl.appendChild(card);

    const button = card.querySelector(".applied-btn");
    button.addEventListener("click", async () => {
      const newApplied = !project.applied;
      try {
        await updateApplied(project.id, newApplied);
        project.applied = newApplied;
        button.textContent = project.applied ? "応募済み" : "応募する";
        button.classList.toggle("done", project.applied);
      } catch (error) {
        alert(`更新に失敗しました: ${error.message}`);
      }
    });

    const favoriteButton = card.querySelector(".favorite-btn");
    favoriteButton.addEventListener("click", () => {
      const nowFavorite = toggleFavorite(project.id);
      favoriteButton.textContent = nowFavorite ? "★" : "☆";
      favoriteButton.classList.toggle("active", nowFavorite);
    });

    //案件削除
    const deleteButton = card.querySelector(".delete-btn");
    const deleteConfirm = card.querySelector(".delete-confirm");
    const confirmDeleteButton = card.querySelector(".confirm-delete-btn");
    const cancelDeleteButton = card.querySelector(".cancel-delete-btn");

    deleteButton.addEventListener("click", () => {
      deleteConfirm.hidden = false;
    });

    cancelDeleteButton.addEventListener("click", () => {
      deleteConfirm.hidden = true;
    });

    confirmDeleteButton.addEventListener("click", async () => {
      try {
        await deleteProject(project.id);
        allProjects = allProjects.filter((p) => p.id !== project.id);
        applyFilters();
      } catch (error) {
        alert(`削除に失敗しました: ${error.message}`);
      }
    });
  }
} // ← renderListを閉じる}が必要

async function init() {
  renderStatus("読み込み中...");
  try {
    const projects = await getProjectList();

    if (isAppliedViewRequested()) {
      allProjects = projects.filter((project) => project.applied);
      document.querySelector("main h1").textContent = "応募済み案件一覧";
    } else if (isFavoriteViewRequested()) {
      allProjects = projects.filter((project) => isFavorite(project.id));
      document.querySelector("main h1").textContent = "お気に入り案件一覧";
    } else {
      allProjects = projects;
    }

    renderList(allProjects);
  } catch (error) {
    renderStatus(`エラーが発生しました: ${error.message}`);
  }
}

// title / clientName / requiredSkills / location　をまとめて部分一致検索
function filterProjects(keyword) {
  const trimmed = keyword.trim().toLowerCase();
  if (trimmed === "") {
    return allProjects;
  }

  return allProjects.filter((project) => {
    const haystack = [
      project.title,
      project.clientName,
      project.requiredSkills,
      project.location,
    ]
      .join(" ")
      .toLowerCase();

    return haystack.includes(trimmed);
  });
}

keywordInput.addEventListener("input", () => {
  applyFilters();
});

//URLに?applied=true が付いていたら「応募済み」だけの一覧にとぶようにする

function isAppliedViewRequested() {
  const params = new URLSearchParams(window.location.search);
  return params.get("applied") === "true";
}

document.querySelectorAll(".region-tabs .tab").forEach((tabButton) => {
  tabButton.addEventListener("click", () => {
    currentRegion = tabButton.dataset.region;

    document.querySelectorAll(".region-tabs .tab").forEach((btn) => {
      btn.classList.toggle("active", btn === tabButton);
    });

    applyFilters();
  });
});

const allCategoriesButton = document.querySelector(
  '.category-list .category[data-category="all"]',
);

function renderCategoryButtons() {
  document.querySelectorAll(".category-list .category").forEach((btn) => {
    if (btn === allCategoriesButton) {
      btn.classList.toggle("active", currentCategories.size === 0);
    } else {
      btn.classList.toggle(
        "active",
        currentCategories.has(btn.dataset.category),
      );
    }
  });
}

//カテゴリ検索
document
  .querySelectorAll(".category-list .category")
  .forEach((categoryButton) => {
    categoryButton.addEventListener("click", () => {
      if (categoryButton === allCategoriesButton) {
        currentCategories.clear();
      } else {
        const category = categoryButton.dataset.category;
        if (currentCategories.has(category)) {
          currentCategories.delete(category);
        } else {
          currentCategories.add(category);
        }
      }

      renderCategoryButtons();
      applyFilters();
    });
  });

init();
