# CakeOrderingApp

My first firebase project, staying focused on learning new things and using skills I have already learnt. So far app allows user to sign up(with google, facebook or email address ) order a bake good, pay for it (or not), follow his orders, check orders history, check shopping cart. I look at this project as a template in case I'd need to build a commerce app like shop, barber studio app where customers can book services or anything else.

I think app would look better if the pics were a bit more professional(right now I'm using my own pictures, of my own stuff!

### 26/03/2022
 Over 3 weeks ago when I was told that Salutorget is going to be closed for 3 weeks due to covid-19 I have set myself a target for this lockdown, this app, to focus on something, to have a reason to wake up early for a reason and stay motivated. Now it's sort of deadline for this work I have set for myself. I am quite happy with result, all of the functionality which I wanted got implemented, I will still have to improve layout, and I would like to give admin more control of uploading picture for product, meybe make pictures in array and picture in presented product fragment a recycler view, we will see. I'll come back to improving layout and fixing warnings soon, rest for later.


## Screenshots

<p align="center">
 <img src="https://user-images.githubusercontent.com/70368829/112008343-0f840980-8b2e-11eb-96b9-cf85604e6054.png" width="320">
   <img src="https://user-images.githubusercontent.com/70368829/112008351-10b53680-8b2e-11eb-8b4d-15106a3f40db.png" width="320">
 <img src="https://user-images.githubusercontent.com/70368829/112008362-14e15400-8b2e-11eb-92bb-53890be243c8.png" width="320">
  <img src="https://user-images.githubusercontent.com/70368829/112008366-14e15400-8b2e-11eb-97df-c8dba188156b.png" width="320">
   <img src="https://user-images.githubusercontent.com/70368829/112008372-1743ae00-8b2e-11eb-98b9-ace3f9c0b1fc.png" width="320">
   <img src="https://user-images.githubusercontent.com/70368829/112008376-17dc4480-8b2e-11eb-9ecc-02cda98edd04.png" width="320">
   <img src="https://user-images.githubusercontent.com/70368829/112008385-1a3e9e80-8b2e-11eb-8fd6-f2255218f722.png" width="320">
</p>


## Technologies used: 
- Firebase(Firebase auth, Firestore ,Firebase storage)
- Stripe
- Glide
- MVVM pattern
- Google authentication
- Facebook authetication
- Email Sign up and sign in


## Features implemented
- Authentication
- Online Database, FIREBASE
- Online payment, STRIPE
- Shopping cart
- Placed orders list
- Placed orders history
- Presentation of store
- Presentation of baked goods
- Store contact options
- Calendar (Let's say shop cannot handle to bake more than 3 birthday cakes a day due to space limit I want calendar when user can see if birthday cakes are available for this day)
- Admin account (Checking orders, signing them as completed, adding new products to the shop. )

## TODO features
- Better layout and GUI(I don't like how app looks but it's still in progress)
- Cutting picture when adding product as admin


### Main targets of this app : 
- Presentation of the store
- Contact 
- Option to order a bake good like bday cake, muffins
- Option to pay for order
- Firebase authentication with google, facebook, email
- Admin account which can mark order as Done 
- Calendar which show available days to order a birthday cake( let's say the store is a small startup with limited workers and space and they can't make 100 birthday cakes a day.)
