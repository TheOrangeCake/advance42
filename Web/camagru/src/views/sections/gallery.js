export function gallery({images, hasNext, page}) {


	return (
		`<section id="body-wrapper">
			<div id="gallery">

			</div>
			<div id="page-selector">
				<a href="/gallery?page=${page === 1 ? page : page - 1}" class="page-button" ${page === 1 ? "hidden" : ""}>Prev</a>
				<div>${page}</div>
				<a href="/gallery?page=${page + 1}" class="page-button" ${hasNext ? "" : "hidden"}>Next</a>
			</div>
		</section>`
	)
}
