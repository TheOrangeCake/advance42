### TODO
- navbar and footer responsive
- cap height and width of uploaded png
- add 1 more bonus for full point

# Camagru

Camagru is a small web app built for the advance 42 curriculum. Users sign up with an email confirmation, then take a webcam photo (or upload an image) and add predefined stickers that they can move and resize. The final image is composited on the server. Every picture is shown in a public gallery, paginated and sorted by date, where signed-in users can like and comment.

## Tech stack
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

## User
### Authentication
- Sign in
    - Username and password
- Sign up
    - Email, username and password
    - Password salted and complex
    - Email confirmation through link
    - Forget password
- Sign out

### Authorization
- Modify username, email and password
- Like and comment pictures
- Setting to turn off notification
- Edit pictures
- Delete their own pictures

## Gallery
### Public directory
- By all users
- Sorted by creation date
- Pagination (5 per page min)
### Interactive
- Like and comment
- Delete own pictures
- Notification when comment received

## Editing
### Preview
- Webcam
- Upload with format and size validation
- Button to capture disable if no sticker
### Sticker
- Predefined list
- Selectable and movable
- Final picture creation server side
### History
- Thumbnails of previous taken pictures
</br>

</br>
</br>


# External package
- [bcrypt](https://www.npmjs.com/package/bcrypt) : encrypt password
- [postgres](https://www.npmjs.com/package/pg) : database manipulation
- [nodemailer](https://nodemailer.com/) : email handling
- [jimp](https://www.npmjs.com/package/jimp) : image handling
</br>

</br>
</br>

# Bonus
- Live preview
- Multiple stickers
- Stickers resize
- Stickers change position
</br>

</br>
</br>

## Debug

Print the users table to the terminal (from the project root, containers running):
```sh
docker compose -f camagru.yaml exec camagru-postgresql sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "SELECT id, username, email, active, mail_token_exp, created_at FROM users ORDER BY id;"'
```

