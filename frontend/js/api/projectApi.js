// TODO: GET /api/projects を叩いて一覧を返す getProjectList() を実装する
// 参考: BASE_URL = "http://localhost:8080/api/projects"

const BASE_URL = "http://localhost:8080/api/projects";

async function getProjectList(){
    const res = await fetch(BASE_URL);

    if(!res.ok){
        const body = await res.json().catch(() => null);
        const message = body?.error?.message ?? `一覧の取得に失敗しました (status: ${res.status})`;
        throw new Error(message);
    }

    return res.json(); //Project[]の配列を返す
}

//詳細表示は、ページ遷移
async function getProjectDetail(id){
    const res = await fetch(`${BASE_URL}/${id}`);

    if(!res.ok){
        const body = await res.json().catch(() => null);
        const message = body?.error?.message ?? `詳細の取得に失敗しました（Status:${res.status})`;
        throw new Error(message);
    }

    return res.json();
}

//応募済み（applied）の状態をサーバーに更新する関数
async function updateApplied(id, applied) {
    const res = await fetch(`${BASE_URL}/${id}/applied`,{
        method: "PUT",
        headers: {"Content-Type":"application/json"},
        body: JSON.stringify({applied}),
    });

    if(!res.ok) {
        const body =await res.json().catch(() => null);
        const message = body?.error?.message?? `更新に失敗しました(status: ${res.status})`;
        throw new Error(message);
    }
}

async function createProject({title, clientName, requiredSkills, location, priceMin, priceMax, category}) {
    const res = await fetch(BASE_URL, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({title, clientName, requiredSkills, location, priceMin, priceMax, category}),
    });

    if(!res.ok) {
        const body = await res.json().catch(() => null);
        const message = body?.error?.message?? `登録に失敗しました: (status: ${res.status})`;
        throw new Error(message);
    }

    return res.json();
}

async function deleteProject(id) {
    const res = await fetch(`${BASE_URL}/${id}`,{method:"DELETE"});

    if(!res.ok) {
        const body = await res.json().catch(() => null);
        const message = body?.error?.message?? `更新に失敗しました(staus: ${res.status})`;
        throw new Error(message);
    }
}

export { getProjectList, getProjectDetail, updateApplied, createProject ,deleteProject };