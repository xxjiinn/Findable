document.addEventListener("DOMContentLoaded", () => {
    const saveContentFeature = document.getElementById("saveContentFeature");
    const makeTagFeature = document.getElementById("makeTagFeature");

    saveContentFeature.addEventListener("click", () => {
        window.location.href = "/post";  // 클릭 시 게시물 페이지로 이동
    });
    makeTagFeature.addEventListener("click", () => {
        window.location.href = "/tag";
    });

    const features = document.querySelectorAll(".feature");
    features.forEach(feature => {
        feature.addEventListener("click", () => {
            feature.classList.toggle("active");
        });
    });
});
