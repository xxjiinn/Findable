// 버튼 클릭 시 화면 전환 기능
function navigateToCreatePost() {
    toggleSection("createPostSection");
}

function navigateToPostList() {
    toggleSection("postListSection");
    loadPosts(); // 게시물 목록 불러오기
}

function toggleSearch() {
    toggleSection("searchSection");
}

// 뒤로 가기 버튼 기능
function goBackToMain() {
    toggleSection("mainOptions");
}

// 공통 함수: 섹션 전환
function toggleSection(sectionId) {
    const sections = ["mainOptions", "createPostSection", "postListSection", "searchSection"];
    sections.forEach(id => {
        document.getElementById(id).classList.add("hidden");
    });
    document.getElementById(sectionId).classList.remove("hidden");
}

// 게시물 목록 불러오기 함수 (예제)
async function loadPosts() {
    const response = await fetch('/api/post');
    const posts = await response.json();

    const postsContainer = document.getElementById('posts');
    postsContainer.innerHTML = ''; // 기존 목록 초기화

    posts.forEach(post => {
        const postElement = document.createElement('div');
        postElement.textContent = post.title; // 게시물 제목만 표시 (예제)
        postsContainer.appendChild(postElement);
    });
}

// 게시물 검색 함수 (예제)
async function searchPosts() {
    const query = document.getElementById('searchInput').value;
    const response = await fetch(`/api/post/search?query=${encodeURIComponent(query)}`);
    const results = await response.json();

    const searchResults = document.getElementById('searchResults');
    searchResults.innerHTML = ''; // 기존 검색 결과 초기화

    results.forEach(result => {
        const resultElement = document.createElement('div');
        resultElement.textContent = result.title; // 검색 결과 제목 표시
        searchResults.appendChild(resultElement);
    });
}
