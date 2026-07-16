import { getProjectDetail, updateProject } from "./api/projectApi.js";

const formEl = document.getElementById("edit-form");
const statusEl = document.getElementById("status-message");

function renderStatus(message) {
  statusEl.textContent = message;
  statusEl.hidden = message === "";
}

function getIdFromUrl() {
  const params = new URLSearchParams(window.location.search);
  return params.get("id");
}

function fillForm(project) {
  formEl.elements["title"].value = project.title ?? "";
  formEl.elements["clientName"].value = project.clientName ?? "";
  formEl.elements["requiredSkills"].value = project.requiredSkills ?? "";
  formEl.elements["location"].value = project.location ?? "";
  formEl.elements["priceMin"].value = project.priceMin ?? "";
  formEl.elements["priceMax"].value = project.priceMax ?? "";
  formEl.elements["status"].value = project.status ?? "OPEN";
  formEl.elements["category"].value = project.category ?? "";

}

async function init() {
  const id = getIdFromUrl();

  if (!id) {
    renderStatus("案件番号が指定されていません。");
    return;
  }

  renderStatus("読み込み中...");
  try {
    const project = await getProjectDetail(id);
    fillForm(project);
    renderStatus("");
    formEl.hidden = false;
  } catch (error) {
    renderStatus(`エラーが発生しました: ${error.message}`);
    return;
  }

  formEl.addEventListener("submit", async (event) => {
    event.preventDefault();

    const formData = new FormData(formEl);
    const title = formData.get("title").trim();
    const clientName = formData.get("clientName").trim();
    const requiredSkills = formData.get("requiredSkills").trim();
    const location = formData.get("location").trim();
    const priceMin = formData.get("priceMin");
    const priceMax = formData.get("priceMax");
    const status = formData.get("status");
    const category = formData.get("category").trim();

    if (priceMin !== "" && priceMax !== "" && Number(priceMax) < Number(priceMin)){
    renderStatus("上限は下限以上の値を入力してください");
    return;
  }

    renderStatus("更新中...");

    try {
      await updateProject(id, { title, clientName, requiredSkills, location, priceMin, priceMax, status, category});
      window.location.href = "index.html";
    } catch (error) {
      renderStatus(`更新に失敗しました: ${error.message}`);
    }
  });
}

init();
