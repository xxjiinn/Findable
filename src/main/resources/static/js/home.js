document.addEventListener("DOMContentLoaded", () => {
    const features = document.querySelectorAll(".feature");

    features.forEach(feature => {
        feature.addEventListener("click", () => {
            feature.classList.toggle("active");
        });
    });
});
