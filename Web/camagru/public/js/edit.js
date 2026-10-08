/* video capture */
let streaming = false;
let uploaded = false;
let isSending = false;
let imgSelection = new Map(); // name -> { x, y, w, el }

const video = document.querySelector("#video");
const cameraWarn = document.querySelector("#camera-warn");
const canvas = document.querySelector("#canvas");
const startButton = document.querySelector("#start-button");
const allowButton = document.querySelector("#permissions-button");
const cameraWarnMessage = document.querySelector("#camera-warn-message");
const stickerList = document.querySelector("#sticker-list");
const uploadButton = document.querySelector("#upload-button");
const uploadInput = document.querySelector("#upload-input");
const uploadPreview = document.querySelector("#upload-preview");
const stickerLayer = document.querySelector("#sticker-layer");
const history = document.querySelector("#history-wrapper");

startButton.disabled = true;

/* upload */
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

/* allow access camera */
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
			video.play().catch((err) => {
				if (err.name === "AbortError") {
					return;
				}
				stopCamera();
				cameraWarnMessage.textContent = "Could not start the camera, please upload an image instead";
				cameraWarn.style.display = "flex";
				allowButton.style.display = "none";
			});
		})
		.catch(() => {
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
}

resetCanvas();

/* take picture */
async function takePicture() {
	const context = canvas.getContext("2d");
	const source = uploaded ? uploadPreview : video;
	const width = uploaded ? uploadPreview.naturalWidth : video.videoWidth;
	const height = uploaded ? uploadPreview.naturalHeight : video.videoHeight;
	if (!startButton.disabled && (streaming || uploaded) && width && height) {
		startButton.disabled = true;
		canvas.width = width;
		canvas.height = height;
		context.drawImage(source, 0, 0, width, height);

		// send img and meta back to backend
		const img = canvas.toDataURL("image/png");
		try {
			isSending = true;
			const response = await fetch("/api/compose", {
				method: "POST",
				headers: {"Content-Type": "application/json"},
				body: buildComposeBody(img),
			});
			if (!response.ok) {
				alert(await response.text());
				return;
			}
			updateHistory(await response.json());
		} catch (e) {
			alert(e.message);
		} finally {
			isSending = false;
			updateTakeButton();
		}
	} else {
		resetCanvas();
	}
}

function updateTakeButton() {
	const selected = imgSelection.size > 0;
	startButton.disabled = !((streaming || uploaded) && selected && !isSending);
}

function buildComposeBody(img) {
	const payload = {img: img, stickers: []};
	imgSelection.forEach((value, key) => {
		payload.stickers.push({name: key, meta: {x: value.x, y: value.y, w: value.w}});
	});
	return JSON.stringify(payload);
}

function updateHistory(res) {
	const item = document.createElement("div");
	item.classList.add("history-item");
	item.setAttribute("data-image-history-id", res.id);

	const composedImg = document.createElement("img");
	composedImg.src = res.url;
	composedImg.classList.add("history-img");

	const deleteButton = document.createElement("button");
	deleteButton.type = "button";
	deleteButton.classList.add("history-delete");

	item.append(composedImg, deleteButton);
	history.insertBefore(item, history.firstChild ?? null);
}

/* delete image from history */
history?.addEventListener("click", async (ev) => {
	const button = ev.target.closest(".history-delete");
	if (button === null || button.disabled) {
		return;
	}
	const item = button.closest(".history-item");
	const id = Number(item.getAttribute("data-image-history-id"));

	button.disabled = true;
	try {
		const response = await fetch("/api/delete", {
			method: "DELETE",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify({id: id}),
		});
		if (!response.ok) {
			alert(await response.text());
			return;
		}
		item.remove();
	} catch (e) {
		alert(e.message);
	} finally {
		button.disabled = false;
	}
});

/* add/remove sticker from preview */
const DEFAULT_X = 0.5;
const DEFAULT_Y = 0.5;
const DEFAULT_W = 0.2;
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
		el: document.createElement("div")
	};
	entry.el.setAttribute("data-image-preview", name);
	entry.el.classList.add("placed-sticker");

	// create the sticker
	const img = document.createElement("img");
	img.src = sticker.src;
	img.draggable = false;

	// create the slider
	const slider = document.createElement("input");
	slider.type = "range";
	slider.className = "sticker-size";
	slider.min = "0.05";
	slider.max = "0.6";
	slider.step = "0.01";
	slider.value = String(entry.w);
	slider.addEventListener("input", () => {
		entry.w = Number(slider.value);
		renderSticker(entry);
	});

	entry.el.append(img, slider);
	renderSticker(entry);
	stickerLayer.appendChild(entry.el);
	imgSelection.set(name, entry);
}

function renderSticker(entry) {
	entry.el.style.left = `${entry.x * 100}%`;
	entry.el.style.top = `${entry.y * 100}%`;
	entry.el.style.width = `${entry.w * 100}%`;
	if (imgSelection.get(activeSticker) === entry) {
		placeSlider(entry);
	}
}

function removeSticker(name) {
	const entry = imgSelection.get(name);
	if (entry) {
		setActive(null);
		entry.el.remove();
		imgSelection.delete(name);
	}
}

// sticker resize bar placement
function placeSlider(entry) {
	const slider = entry.el.querySelector(".sticker-size");
	const style = getComputedStyle(slider);
	const needed = slider.offsetHeight + (parseFloat(style.marginTop) || 0) + (parseFloat(style.marginBottom) || 0);
	const layerRect = stickerLayer.getBoundingClientRect();
	const stickerRect = entry.el.getBoundingClientRect();
	const fitsBelow = layerRect.bottom - stickerRect.bottom >= needed;
	const fitsAbove = stickerRect.top - layerRect.top >= needed;
	entry.el.classList.toggle("slider-above", !fitsBelow && fitsAbove);
	entry.el.classList.toggle("slider-inside", !fitsBelow && !fitsAbove);
}

/* sticker manipulation: drag */
let activeSticker = null;
let drag = null;

stickerLayer?.addEventListener("pointerdown", (ev) => {
	if (streaming || uploaded) {
		if (ev.target.closest(".sticker-size")) {
			return;
		}

		const img = ev.target.closest(".placed-sticker");
		if (img === null) {
			setActive(null);
			return;
		}
		setActive(img);
		const name = img.getAttribute("data-image-preview");
		const entry = imgSelection.get(name);
		if (!entry) {
			return;
		}

		const pos = pointerToFraction(ev);
		drag = {
			entry,
			pointerId: ev.pointerId,
			offsetX: pos.x - entry.x,
			offsetY: pos.y - entry.y
		};
		entry.el.setPointerCapture(ev.pointerId);
	}
})

stickerLayer?.addEventListener("pointermove", (ev) => {
	if (!drag || ev.pointerId !== drag.pointerId) {
		return;
	}

	const pos = pointerToFraction(ev);
	drag.entry.x = Math.min(1, Math.max(0, pos.x - drag.offsetX));
	drag.entry.y = Math.min(1, Math.max(0, pos.y - drag.offsetY));
	renderSticker(drag.entry);
})

function pointerToFraction(ev) {
	const rect = stickerLayer.getBoundingClientRect();
	return {
		x: (ev.clientX - rect.left) / rect.width,
		y: (ev.clientY - rect.top) / rect.height
	};
}

stickerLayer?.addEventListener("pointerup", () => {
	drag = null;
})

stickerLayer?.addEventListener("pointercancel", () => {
	drag = null;
})

function setActive(img) {
	const name = img ? img.getAttribute("data-image-preview") : null;
	if (name === activeSticker) {
		return;
	}

	// remove current actived slider
	const currentImg = imgSelection.get(activeSticker)?.el;
	if (currentImg !== undefined) {
		currentImg.querySelector(".sticker-size").classList.remove("active");
	}
	activeSticker = null;
	if (!img) {
		return;
	}

	// active slider
	img.querySelector(".sticker-size").classList.add("active");
	activeSticker = name;
	placeSlider(imgSelection.get(name));
}

