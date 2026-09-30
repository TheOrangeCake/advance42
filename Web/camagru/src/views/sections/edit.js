import { getStickerUrl } from "../../services/stickers.js"

export function edit(stickers) {
	return (
		`<section id="body-wrapper">
			<div id="main-wrapper">
				<div id="camera">
					<div id="camera-warn">
						<h3>Please enable camera first</h3>
						<button type="button" id="permissions-button">Allow camera</button>
					</div>
					<video id="video">Video stream not available.</video>
					<div id="camera-buttons">
						<button type="button" class="button" id="start-button">Capture photo</button>
						<button type="button" class="button" id="upload-button">Upload image</button>
					</div>
				</div>

				<div id="sticker-wrapper">
					<div id="sticker-list">
						${stickers.map(sticker => `<img class="sticker" src="${getStickerUrl(sticker)}">`).join("")}
					</div>
				</div>
			</div>
			<canvas id="canvas"></canvas>
			<div class="output">
				<img id="photo" src="" alt="The screen capture will appear in this box." />
			</div>
		</section>`
	)
}
