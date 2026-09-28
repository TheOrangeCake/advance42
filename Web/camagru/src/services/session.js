import { generateToken } from "../models/user.js";

const SESSION_EXPIRE = 7 * 24 * 60 * 60 * 1000;  // 7 days
const sessionTable = new Map();

export function createSession(user) {
	const token = generateToken();
	const { id , username, email } = user;
	sessionTable.set(token, {id, username, email, expireAt: Date.now() + SESSION_EXPIRE});
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

// sign out all sessions except current one
export function destroyUserSessions(userId, exceptSessionId) {
	for (const [sessionId, session] of sessionTable) {
		if (session.id === userId && sessionId !== exceptSessionId) {
			sessionTable.delete(sessionId);
		}
	}
}

// update current session with new username/email
export function updateUserSessions(userId, fields) {
	for (const session of sessionTable.values()) {
		if (session.id === userId) {
			Object.assign(session, fields);
		}
	}
}
