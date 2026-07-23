package types

//LoginAttempt is the struct for users attempting to login
type LoginAttempt struct {
	ID   string `json:"id" query:"id"`
	User string `json:"user" query:"user"`
	Pass string `json:"pass" query:"pass"`
}

//RegisterAttempt is the struct for users attempting to register
type RegisterAttempt struct {
	User      string `json:"user"`
	Pass      string `json:"pass"`
	PassCheck string `json:"passcheck"`
}
