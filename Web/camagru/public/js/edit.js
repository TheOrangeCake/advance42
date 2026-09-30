/* video capture */
let streaming = false;

const video = document.querySelector("#video");
const cameraWarn = document.querySelector("#camera-warn");
const canvas = document.querySelector("#canvas");
const startButton = document.querySelector("#start-button");
const allowButton = document.querySelector("#permissions-button");
const photo = document.getElementById("photo"); // remove

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
		console.error(`An error occurred: ${err}`);
		// show error front end
	});
});

video?.addEventListener("canplay", () => {
	streaming = true;
});

startButton?.addEventListener("click", (ev) => {
	takePicture();
	ev.preventDefault();
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
	if (streaming && width && height) {
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
