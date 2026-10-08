import { escapeHtml } from "../../controllers/utils.js";

export function message(title, text) {
	return (
		`<section id="body-wrapper">
			<div id="message-wrapper">
				<h1>${escapeHtml(title)}</h1>
				<p>${escapeHtml(text)}</p>
				<a href="/" class="button">Back to gallery</a>
			</div>
		</section>`
	)
}
