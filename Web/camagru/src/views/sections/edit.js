import { getStickerUrl } from "../../services/stickers.js"

export function edit(stickers, userImages) {
	return (
		`<section id="body-wrapper">
			<div id="main-wrapper">
				<div id="camera">
					<div id="camera-warn">
						<h3 id="camera-warn-message">Please enable camera first</h3>
						<button type="button" id="permissions-button">Allow camera</button>
					</div>
					<div id=camera-screen>
						<video id="video">Video stream not available.</video>
						<img id="upload-preview">
						<div id="sticker-layer"></div>
					</div>
					<div id="camera-buttons">
						<button type="button" class="button" id="start-button">Capture photo</button>
						<button type="button" class="button" id="upload-button">Upload image</button>
						<input type="file" id="upload-input" accept="image/png, image/jpeg" hidden>
					</div>
				</div>

				<div id="sticker-wrapper">
					<div id="sticker-list">
						${stickers
							.map(sticker => `<img class="sticker" data-image-name="${sticker}" src="${getStickerUrl(sticker)}">`)
							.join("")
						}
					</div>
				</div>
			</div>
			<div id="history-wrapper">
				${userImages ? 
					userImages
					.map(img => (
						`<div class="history-item" data-image-history-id="${img.id}">
							<img class="history-img" src="/uploads/${img.filename}">
							<button type="button" class="history-delete"></button>
						</div>`
					))
					.join("")
					: ""
				}
			</div>
			<canvas id="canvas" hidden></canvas>
		</section>`
	)
}
