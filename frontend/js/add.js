import { createProject } from "./api/projectApi.js";

const formEl = document.getElementById("add-form");
const statusEl = document.getElementById("status-message");

function renderStatus(message) {
  statusEl.textContent = message;
  statusEl.hidden = message === "";
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
  const category = formData.get("category").trim();

  renderStatus("登録中...");

  try {
    await createProject({ title, clientName, requiredSkills, location, priceMin, priceMax, category});
    window.location.href = "index.html";
  } catch (error) {
    renderStatus(`登録に失敗しました: ${error.message}`);
  }
});
