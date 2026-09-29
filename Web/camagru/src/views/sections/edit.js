export function edit() {
	return (
		`<section id="body-wrapper">
			<div class="camera">
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
			
			<canvas id="canvas"></canvas>
			<div class="output">
				<img id="photo" src="" alt="The screen capture will appear in this box." />
			</div>
		</section>`
	)
}
