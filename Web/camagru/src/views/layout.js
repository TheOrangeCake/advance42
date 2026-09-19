import { modal } from "./components/modal.js"
import { navbar } from "./sections/navbar.js"
import { footer } from "./sections/footer.js"
import { gallery } from "./sections/gallery.js"

export function layout(title, css, js) {
	const html_modal = modal();
	const html_nav =  navbar();
	const html_footer = footer();
	const html_gallery = gallery();
	return (
		`<!DOCTYPE html>
		<html lang="en">
			<head>
				<meta charset="UTF-8">
				<meta name="viewport" content="width=device-width, initial-scale=1.0">
				<title>${title}</title>
				<link rel="preconnect" href="https://fonts.googleapis.com">
				<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
				<link href="https://fonts.googleapis.com/css2?family=Ubuntu:ital,wght@0,300;0,400;0,500;0,700;1,300;1,400;1,500;1,700&display=swap" rel="stylesheet">
				<link rel="stylesheet" href="/css/global.css">
				<link rel="stylesheet" href="${css}">
				<script src="${js}" defer></script>
			</head>

			<body>
				${html_nav}
				${html_modal}
				${html_gallery}
				${html_footer}
			</body>
		</html>
		`
	)
}
