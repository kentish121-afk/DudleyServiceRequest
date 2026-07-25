# Dudley Service Request Emailer

A simple Android app that lets you compose a service request and send it by email to Dudley Council (`dudleycouncilplus@dudley.gov.uk`).

- Optional personal details (leave blank for anonymous-style reports)
- Your own email is automatically BCC'd so you keep a copy
- Uses the standard Android email Intent (works with Gmail, Outlook, etc.)
- **No login, no MyDudley scraping, no automation of council web forms**

## Important notes

- This is **not** an official council app and does **not** create tracked service requests in MyDudley.
- The council may still prefer you use [MyDudley](https://my.dudley.gov.uk/) or [FixMyStreet](https://www.fixmystreet.com/) for better tracking and photos.
- Email is a legitimate contact method, but response times and logging may differ from online forms.
- Always call **0300 555 2345** for emergencies.

## How to build & run

1. Open the project in **Android Studio** (Hedgehog or newer recommended).
2. Let Gradle sync.
3. Run on an emulator or physical device (min SDK 26).
4. Fill the form → tap **Send Email** → choose your email app → send.

## Project structure

Standard single-module Compose app:
- `app/src/main/java/.../MainActivity.kt` – UI + email Intent logic
- Material 3 theming

## Licence

Public domain / do whatever you want. No warranty.
