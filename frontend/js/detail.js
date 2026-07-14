// TODO: URLの ?id=xx を読み取り、getProjectDetail(id) で1件取得して #project-detail に描画する
// 状態表示は #status-message（読み込み中 / id未指定 / エラー）を使う
//詳細表示のページ作成
import { getProjectDetail } from "./api/projectApi.js";

const detailEl = document.getElementById("project-detail");
const statusEl = document.getElementById("status-message");


const CATEGORY_LABELS = {
    develop: "開発",
    embedded: "組み込み",
    infra: "インフラ",
}

function renderStatus(message) {
    statusEl.textContent = message;
    statusEl.hidden = message === "";
}

//URLの ?id=123を読み取る
function getIdFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

//文字型をDateオブジェクトに変換
function formatDateTime(isoString){
    const date = new Date(isoString);
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hour = date.getHours();
    const minute = String(date.getMinutes()).padStart(2,"0");
    return `${year}年${month}月${day}日 ${hour}時${minute}分`;
}

//詳細表示
function renderDetail(project) {
    detailEl.innerHTML = `
        <h2>${project.title}</h2>
        <div class="id">
        <span class="idLabel">ID:</span>
        <span>${project.id}</span>
        </div>
        <div class="project-info">
            <span class="label">会社名:</span>
            <span>${project.clientName}</span>
            <span class="label">必須スキル:</span>
            <span>${project.requiredSkills}</span>
            <span class="label">勤務地:</span>
            <span>${project.location}</span>
            <span class="label">単価:</span>
            <span>${project.priceMin}円 ~ ${project.priceMax}円</span>
            <span class="label">配属状況:</span>
            <span>${project.status}</span>
            <span class="label">カテゴリ:</span>
            <span>${CATEGORY_LABELS[project.category]??project.category}</span>
            <span class="label">作成日時</span>
            <span>${formatDateTime(project.createdAt)}</span>
            <span class="label">更新日時</span>
            <span>${formatDateTime(project.updatedAt)}</span>
        </div>
    `;
}

//詳細案件のidのバリデーションチェック
async function init(){
    const id = getIdFromUrl();
    
    if(!id){
        renderStatus("案件番号が指定されていません。");
        return;
    }

    renderStatus("読み込み中...");
    try{
        const project = await getProjectDetail(id);
        renderStatus("");
        renderDetail(project);
    }catch (error){
        renderStatus(`エラーが発生しました: ${error.message}`);
    }
}

init();


