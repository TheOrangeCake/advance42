import nodemailer from "nodemailer";

const transporter = nodemailer.createTransport({
	host: process.env.SMTP_HOST,
	port: Number(process.env.SMTP_PORT),
	secure: Number(process.env.SMTP_PORT) === 465,
	auth: {
		user: process.env.SMTP_USER,
		pass: process.env.SMTP_PASS,
	}
})

export async function sendEmail(target, subject, content) {
	try {
		await transporter.verify();
	} catch (e) {
		throw new Error("SMTP server failed", { cause: e });
	}

	const info = await transporter.sendMail({
		from: `"Nguyen NGUYEN" <${process.env.SMTP_USER}>`,
		to: `${target}`,
		subject: `${subject}`,
		text: `${content}`,
	});
	console.log("Email %s sent to %s", info.messageId, target);
}
