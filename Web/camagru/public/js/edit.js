/* video capture */
let streaming = false;
let uploaded = false;
let imgSelection = new Set();

const video = document.querySelector("#video");
const cameraWarn = document.querySelector("#camera-warn");
const canvas = document.querySelector("#canvas");
const startButton = document.querySelector("#start-button");
const allowButton = document.querySelector("#permissions-button");
const photo = document.getElementById("photo"); // remove
const cameraWarnMessage = document.querySelector("#camera-warn-message");
const stickerList = document.querySelector("#sticker-list");

startButton.disabled = true;

allowButton?.addEventListener("click", () => {
	navigator.mediaDevices
		.getUserMedia({ video: true, audio: false })
		.then((stream) => {
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

stickerList?.addEventListener("click", (ev) => {
	const img = ev.target.closest(".sticker");
	if (img === null) {
		return;
	}
	const name = img.getAttribute("data-image-name");
	imgSelection.has(name) ? imgSelection.delete(name) : imgSelection.add(name);
	img.classList.toggle("selected");
	updateTakeButton();
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
	const width = video.videoWidth;
	const height = video.videoHeight;
	if (!startButton.disabled && streaming && width && height) {
		canvas.width = width;
		canvas.height = height;
		context.drawImage(video, 0, 0, width, height);

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
