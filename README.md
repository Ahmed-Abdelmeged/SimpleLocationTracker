## Simple Location Tracker

This project was a challenge project for an interview.

## challenge description

Develop an Android app that enables you to track your walk with images.

The user is starting the app and presses the start button. After that he puts the phone into his pocket and starts walking, every 100 meters a picture for his location is requested from the flickr photo search api and added to his stream. New pictures are added on top.
Any time he takes a look at his phone he sees the most recent picture and can scroll through a stream of pictures which shows him impressions of where he has been. It should work at least for a two hour walk.
The user interface should be simple as shown on the below image.

<a href="https://imgur.com/SfNeEwz"><img src="https://i.imgur.com/SfNeEwz.png?1" title="App"/></a>

## Implementation
The idea of the app is to have a background service to track user location using `FusedLocationProviderClient`. Then every 100 meter, I will make a request to flickr API using `Retrofit` to get pictures with in that lat lng and take the first result from the response. When the image is restored successfully. It will be saved in a Room data base. At the `MainActivity` Using `LiveData` and `Room` i will listen for changes in the database and submit the latest list from the db to the `RecyclerView`. To handle activity configuration change. I used the android architecture component `ViewModel` and `LiveData`. Live data of list of pictures will be stored in the ViewModel and observed by the `Activity` when it's ready. I didn't use `Dagger` for dependency injection, The application was fairly simple but in scalable application it's better to use it to have better testing. The whole application is written in Kotlin 1.3 and take advantage of new features like `inline class`