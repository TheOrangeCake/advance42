import { escapeHtml } from "../../controllers/utils.js";

const UPLOADS_URL = "/uploads/";
const COMMENT_MAX_LENGTH = 500;
const dateFormat = new Intl.DateTimeFormat("en-GB", {
	day: "numeric",
	month: "short",
	year: "numeric",
	hour: "2-digit",
	minute: "2-digit",
});

function plural(count, word) {
	return `${count} ${word}${count === 1 ? "" : "s"}`;
}

export function gallery({images, hasNext, page}, user) {
	const gallery = images.map(image => {
		const img = `<img class="gallery-img" src="${UPLOADS_URL + image.filename}" alt="Gallery image ${image.id}">`;
		const likeIcons = `
			<span class="img-unlike">&#128420;</span>
			<span class="img-like">&#128150;</span>
		`;
		const like = user ?
			`<button type="button" class="gallery-img-like${image.liked ? " liked" : ""}">${likeIcons}</button>` :
			`<div></div>`
		const commentForm = user ? `
			<form class="gallery-img-comment-input">
				<input type="text" class="form-input gallery-comment-input-field" name="comment" maxlength="${COMMENT_MAX_LENGTH}" required>
				<input type="submit" value="Comment" class="button gallery-submit-btn">
			</form>
		` : "";
		const right_panel = `
			<div class="gallery-right-panel">
				<div class="gallery-img-top-bar">
					${like}
					<div class="gallery-img-meta">
						<span class="gallery-img-like-count">${plural(image.likeCount, "like")}</span>
						<span class="gallery-img-comment-count">${plural(image.commentCount, "comment")}</span>
					</div>
				</div>
				<div class="gallery-img-comments">
					${image.comments.map(comment => {
						return `
							<div class="gallery-img-comments-comment">
								<div class="gallery-img-username">${escapeHtml(comment.username)}</div>
								<div class="gallery-img-comment">${escapeHtml(comment.comment)}</div>
								<div class="gallery-img-date">${dateFormat.format(comment.createdAt)}</div>
							</div>
						`
					}).join("")}
				</div>
				${commentForm}
			</div>
		`;
		return `<div class="gallery-img-wrapper" data-image-id="${image.id}">` + img + right_panel + `</div>`;
	}).join("");

	return (
		`<section id="body-wrapper">
			<div id="gallery">
				${gallery}
			</div>
			<div id="page-selector">
				<a href="/gallery?page=${page === 1 ? page : page - 1}" class="page-button${page === 1 ? " invisible" : ""}">Prev</a>
				<div>${page}</div>
				<a href="/gallery?page=${page + 1}" class="page-button${hasNext ? "" : " invisible"}">Next</a>
			</div>
		</section>`
	)
}
