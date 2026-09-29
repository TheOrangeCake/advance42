# Camagru

## Base architecture
|Name | Tech |
|:---------|:--------------------|
|Webserver |Nginx                |
|Container |Docker Compose       |
|Frontend  |HTML, CSS, JavaScript|
|Backend   |Node.js              |
|DB        |Postgres             |
|Email     |Nodemailer           |
|Auth      |Session cookie       |
</br>

</br>
</br>

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

</br>
</br>
</br>

# Step overview
<ol>
    <li>Scaffolding
        <ul>
            <li>OK - Scaffolding HTML</li>
            <li>OK - Scaffolding Nginx</li>
            <li>OK - Scaffolding Dockerfile + Docker Compose</li>
            <li>OK - Makefile</li>
        </ul>
    </li>
    <li>OK - Quick layout design</li>
    <li>Implement User feature
        <ol>
            <li>OK - Sign up</li>
            <li>OK - Sign in</li>
            <li>OK - Sign out</li>
            <li>OK - Forgot password</li>
        </ol>
    </li>
    <li>Implement Editing feature
        <ol>
            <li>Preview</li>
            <li>Superposable</li>
            <li>History</li>
        </ol>
    </li>
    <li>Implement Gallery feature
        <ol>
            <li>Public directory</li>
            <li>Interactive</li>
        </ol>
    </li>
    <li>Security</li>
</ol>

Print the users table to the terminal (from the project root, containers running):
```sh
docker compose -f camagru.yaml exec camagru-postgresql sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "SELECT id, username, email, active, mail_token_exp, created_at FROM users ORDER BY id;"'
```


# External package
- [bcrypt](https://www.npmjs.com/package/bcrypt)
- [postgres](https://www.npmjs.com/package/pg)
- [nodemailer](https://nodemailer.com/)
- [jimp](https://www.npmjs.com/package/jimp)
