const gallery = document.querySelector("#gallery");

/* like */
gallery?.addEventListener("click", async (ev) => {
	await like(ev);
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

/* comment */
gallery?.addEventListener("submit", async (ev) => {
	ev.preventDefault();
	await comment(ev);
})

const COMMENT_MAX_LENGTH = 500;
const COMMENT_MIN_LENGTH = 0;

async function comment(ev) {
	const commentForm = ev.target.closest(".gallery-img-comment-form");
	if (!commentForm) {
		return;
	}

	const commentInput = commentForm.querySelector(".gallery-comment-input");
	const commentBtn = commentForm.querySelector(".gallery-submit-btn");
	if (!commentInput || !commentBtn) {
		return;
	}
	
	const img = ev.target.closest(".gallery-img-wrapper");
	const imgId = Number(img.getAttribute("data-image-id"));

	try {
		const commentTxt = commentInput.value.trim();
		
		commentBtn.disabled = true;
		commentInput.disabled = true;

		if (commentTxt.length > COMMENT_MAX_LENGTH) {
			alert(`Comment max length is ${COMMENT_MAX_LENGTH} characters`);
			return;
		}
		if (commentTxt.length <= COMMENT_MIN_LENGTH) {
			alert("Comment is empty");
			return;
		}

		const response = await fetch("/api/comment", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify({
				id: imgId,
				comment: commentTxt,
			})
		})
		if (!response.ok) {
			alert(await response.text());
			return;
		}

		updateComment(await response.json(), img);
		commentForm.reset();
	} catch (e) {
		alert(`Comment submit error: ${e.message}`);
	} finally {
		commentBtn.disabled = false;
		commentInput.disabled = false;
	}
}

function updateComment(res, img) {
	const commentCount = img.querySelector(".gallery-img-comment-count");
	commentCount.textContent = plural(res.commentCount, "comment");

	const username = document.createElement("div");
	username.classList.add("gallery-img-username");
	username.textContent = res.username;

	const comment = document.createElement("div");
	comment.classList.add("gallery-img-comment");
	comment.textContent = res.comment;

	const createdAt = document.createElement("div");
	createdAt.classList.add("gallery-img-date");
	createdAt.textContent = res.createdAt;

	const commentWrapper = document.createElement("div");
	commentWrapper.classList.add("gallery-img-comments-comment");
	commentWrapper.append(username, comment, createdAt);

	const commentsSection = img.querySelector(".gallery-img-comments");
	commentsSection.appendChild(commentWrapper);
}


/* utils */
function plural(count, word) {
	return `${count} ${word}${count === 1 ? "" : "s"}`;
}
