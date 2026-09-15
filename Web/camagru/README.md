# Camagru

## Base architecture
|Name | Tech |
|:---------|:-------------------------|
|Webserver |Nginx                     |
|Container |Docker                    |
|Frontend  |HTML, Tailwind, Typescript|
|Backend   |TBD                       |
|DB        |Postgresql                |
|ORM       |TBD (raw query to learn?) |
|Email     |TBD                       |
|Auth      |JWT                       |


# Features
- User
    - Authentication
        - Sign in
            - Username and password
        - Sign up
            - Email, username and password
            - Password salted and complex
            - Email confirmation through link
            - Forget password
        - Sign out
    - Authorization
        - Modify username, email and password
        - Like and comment pictures
        - Setting to turn off notification (default true)
        - Edit pictures
        - Delete their own pictures

- Gallery
    - Public directory
        - By all users
        - Sorted by creation date
        - Pagination (5 per page min)
    - Interactive
        - Like and comment
        - Delete own pictures
        - Notification when comment received

- Editing
    - Preview
        - Webcam
        - Upload with format and size validation
        - Button to capture disable if no superposable
    - Superposable
        - Predefined list
        - Selectable and movable
        - Final picture creation server side
    - History
        - Thumbnails of previous taken
