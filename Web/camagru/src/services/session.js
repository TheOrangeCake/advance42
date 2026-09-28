import { generateToken } from "../models/user.js";

const SESSION_EXPIRE = 7 * 24 * 60 * 60 * 1000;  // 7 days
const sessionTable = new Map();

export function createSession(user) {
	const token = generateToken();
	const { id , username } = user;
	sessionTable.set(token, {id, username, expireAt: Date.now() + SESSION_EXPIRE});
	return token;
}

export function getSession(sessionId) {
	const session = sessionTable.get(sessionId);
	if (session === undefined) {
		return null;
	}
	if (Date.now() >= session.expireAt) {
		sessionTable.delete(sessionId);
		return null;
	}
	return session;
}

export function destroySession(sessionId) {
	return sessionTable.delete(sessionId);
}

export function destroyUserSessions(userId) {
	for (const [sessionId, session] of sessionTable) {
		if (session.id === userId) {
			sessionTable.delete(sessionId);
		}
	}
}
