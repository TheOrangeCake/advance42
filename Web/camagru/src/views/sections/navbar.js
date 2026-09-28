import { escapeHtml } from "../../controllers/utils.js";

export function navbar(user) {
	const username = user?.username;
	return (
		`<nav class="nav">
			<a class="logo" href="/">
				<h1>42 Camagru</h1>
				<h5 id="slogan">by hoannguy</h5>
			</a>
			<div class="nav-items">
				<a href="/">Gallery</a>
				${
					username ?
					`<a href="/">Create picture</a>
					<a href="/profile">${ escapeHtml(username) }</a>` :
					``
				}
			</div>
			${
				user ?
				`<button type="button" class="button" id="signout-btn">Sign out</button>` :
				`<button type="button" class="button" id="signin-btn">Sign in</button>` 
			}
		</nav>`
	)
}
