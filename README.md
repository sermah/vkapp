# Sample VK App

Маленький клиент ВК.

Написано на Kotlin, с использованием Jetpack Compose и VK Android SDK.
Везде используется Material3 в светлой теме.

## Функционал

- Авторизация через VK SDK
- Просмотр стены пользователя (пока id = 1, то есть стена Павла Дурова)
    - Подгрузка фото пользователя
    - Числа на счетчиках лайков и т.д. сокращаются в K/M/B (4523 -> 4,5К)
    - Длинные посты обрезаются под кнопку "Show more"
    - (пока что) Просмотр только фотографий
- Ставить/убирать лайки
    - Мгновенный фидбек в виде изменения цвета кнопки
- Карусель фотографий
    - Соотношение сторон от 1:1 до 2:1, остальное вмещается в центр
- Анимации сжатия шапки при скролле, изменения цветов кнопок

## Скриншоты

### Шапка профиля

<img alt="Header" src="screenshots/head.png" width="200"/>

### Пост с картинкой

<img alt="Post with photo" src="screenshots/photo_1.png" width="200"/>

### Пост с двумя картинками

<span>
<img alt="Post with photos" src="screenshots/photo_2.png" width="200"/>
<img alt="Post with photos 2" src="screenshots/photo_21.png" width="200"/>
</span>

### Пост с раскрытым Show more

<img alt="Post with opened show more" src="screenshots/photo_more.png" width="200"/>
