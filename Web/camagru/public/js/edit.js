/* video capture */
let streaming = false;
let uploaded = false;
let imgSelection = new Map(); // name -> { x, y, w, el }

const video = document.querySelector("#video");
const cameraWarn = document.querySelector("#camera-warn");
const canvas = document.querySelector("#canvas");
const startButton = document.querySelector("#start-button");
const allowButton = document.querySelector("#permissions-button");
const photo = document.getElementById("photo"); // remove
const cameraWarnMessage = document.querySelector("#camera-warn-message");
const stickerList = document.querySelector("#sticker-list");
const uploadButton = document.querySelector("#upload-button");
const uploadInput = document.querySelector("#upload-input");
const uploadPreview = document.querySelector("#upload-preview");
const stickerLayer = document.querySelector("#sticker-layer");

startButton.disabled = true;

uploadButton?.addEventListener("click", () => {
	uploadInput.click();
});

uploadInput?.addEventListener("change", () => {
	const file = uploadInput.files[0];
	uploadInput.value = "";
	if (!file || !file.type.startsWith("image/")) {
		return;
	}
	if (uploadPreview.src) {
		URL.revokeObjectURL(uploadPreview.src);
	}
	uploaded = false;
	updateTakeButton();
	uploadPreview.src = URL.createObjectURL(file);
});

uploadPreview?.addEventListener("load", () => {
	stopCamera();
	cameraWarn.style.display = "none";
	uploadPreview.style.display = "flex";
	uploaded = true;
	updateTakeButton();
});

uploadPreview?.addEventListener("error", () => {
	uploaded = false;
	updateTakeButton();
	uploadPreview.style.display = "none";
	cameraWarnMessage.textContent = "Could not read this image, please try another one";
	if (!streaming) {
		cameraWarn.style.display = "flex";
	}
});

function stopCamera() {
	if (video.srcObject) {
		video.srcObject.getTracks().forEach((track) => track.stop());
		video.srcObject = null;
	}
	video.style.display = "none";
	streaming = false;
}

allowButton?.addEventListener("click", () => {
	navigator.mediaDevices
		.getUserMedia({ video: true, audio: false })
		.then((stream) => {
			if (uploaded) {
				stream.getTracks().forEach((track) => track.stop());
				return;
			}
			video.srcObject = stream;
			video.style.display = "flex";
			cameraWarn.style.display = "none";
			video.play();
		})
		.catch((err) => {
			console.log(err.message);
			cameraWarnMessage.textContent = "Problem with camera, please upload an image instead";
			allowButton.style.display = "none";
		});
});

video?.addEventListener("canplay", () => {
	streaming = true;
	updateTakeButton();
});

startButton?.addEventListener("click", (ev) => {
	ev.preventDefault();
	takePicture();
});

function resetCanvas() {
	const context = canvas.getContext("2d");
	context.fillStyle = "#aaaaaa";
	context.fillRect(0, 0, canvas.width, canvas.height);

	const data = canvas.toDataURL("image/png");
	photo.setAttribute("src", data); // remove
}

resetCanvas();

function takePicture() {
	const context = canvas.getContext("2d");
	const source = uploaded ? uploadPreview : video;
	const width = uploaded ? uploadPreview.naturalWidth : video.videoWidth;
	const height = uploaded ? uploadPreview.naturalHeight : video.videoHeight;
	if (!startButton.disabled && (streaming || uploaded) && width && height) {
		canvas.width = width;
		canvas.height = height;
		context.drawImage(source, 0, 0, width, height);

		const data = canvas.toDataURL("image/png");
		// send data back to backend
		photo.setAttribute("src", data); // remove
	} else {
		resetCanvas();
	}
}

function updateTakeButton() {
	const selected = imgSelection.size > 0;
	startButton.disabled = !((streaming || uploaded) && selected);
}

const DEFAULT_X = 0.5;
const DEFAULT_Y = 0.5;
const DEFAULT_W = 200;
stickerList?.addEventListener("click", (ev) => {
	if (streaming || uploaded) {
		const img = ev.target.closest(".sticker");
		if (img === null) {
			return;
		}
		const name = img.getAttribute("data-image-name");
		if (imgSelection.has(name)) {
			removeSticker(name);
		} else {
			addSticker(img, name);
		}
		img.classList.toggle("selected");
		if (imgSelection.size > 0 && (streaming || uploaded)) {
			stickerLayer.style.display = "block";
		} else {
			stickerLayer.style.display = "none";
		}
		updateTakeButton();
	}
});

function addSticker(sticker, name) {
	const entry = {
		x: DEFAULT_X,
		y: DEFAULT_Y,
		w: DEFAULT_W,
		el: document.createElement("img")
	};
	entry.el.src = sticker.src;
	entry.el.setAttribute("data-image-preview", name);
	entry.el.classList.toggle("placed-sticker");
	entry.el.style.left = `${entry.x * 100}%`;
	entry.el.style.top = `${entry.y * 100}%`;
	entry.el.style.width = `${entry.w}px`;
	stickerLayer.appendChild(entry.el);
	imgSelection.set(name, entry);
}

function removeSticker(name) {
	const entry = imgSelection.get(name);
	if (entry) {
		entry.el.remove();
		imgSelection.delete(name);
	}
}
