import nodemailer from "nodemailer";
import { internalError } from "../controllers/utils.js";

const transporter = nodemailer.createTransport({
	host: process.env.SMTP_HOST,
	port: Number(process.env.SMTP_PORT),
	secure: Number(process.env.SMTP_PORT) === 465,
	auth: {
		user: process.env.SMTP_USER,
		pass: process.env.SMTP_PASS,
	}
})

export async function sendEmail(target, subject, text, html) {
	try {
		await transporter.verify();
	} catch (e) {
		throw internalError("SMTP server failed", e);
	}

	await transporter.sendMail({
		from: `"Nguyen NGUYEN" <${process.env.SMTP_USER}>`,
		to: `${target}`,
		subject: `${subject}`,
		text: `${text}`,
		...(html && { html }),
	});
}
