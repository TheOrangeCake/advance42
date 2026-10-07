const gallery = document.querySelector("#gallery");

gallery?.addEventListener("click", (ev) => {
	like(ev);
})

async function like(ev) {
	const likeBtn = ev.target.closest(".gallery-img-like");
	if (!likeBtn) {
		return;
	}

	const img = ev.target.closest(".gallery-img-wrapper");
	const imgId = Number(img.getAttribute("data-image-id"));
	const isLiked = likeBtn.classList.contains("liked");

	try {
		likeBtn.disabled = true;
		const response = await fetch("/api/like", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify({
				id: imgId,
				like: !isLiked,
			})
		})
		if (!response.ok) {
			alert(await response.text());
			return;
		}
		updateLike(await response.json(), likeBtn, img);
	} catch (e) {
		alert(e.message);
	} finally {
		likeBtn.disabled = false;
	}
}

function updateLike(res, likeBtn, img) {
	likeBtn.classList.toggle("liked", res.like);
	const likeCount = img.querySelector(".gallery-img-like-count");
	likeCount.textContent = plural(res.likeCount, "like");
}

function plural(count, word) {
	return `${count} ${word}${count === 1 ? "" : "s"}`;
}
