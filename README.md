This program runs best in powershell.
## 1.	Run Server
## 2.	Then run SellerClient and however many BuyerClients you want (each on their own terminal, I tested on the powershell in visual studio code.)
## 3.	Follow the instructions on screen for each client
### 3.1.	Buyer: 
The buyer can wait and watch as sellers goods are displayed to them every 60 seconds. They can enter 1 to leave the market gracefully or they can enter 2 to make a purchase.

If they enter 2 they will be prompted to select which item to choose (this number matches the index of the sellers arrayList). Then they will select the quantity of kgs to buy. A buyer is only allowed to buy from between 1 and 9 kgs, However they can make multiple purchases if they want to.

Then they must just wait for a response from the seller before purchasing again or leaving the market.
### 3.2.	Seller:
The by default is in the market and they have the choice to leave the market by pressing q. They can sit and watch as their goods are being purchased by buyers. They broadcast a message to buyers of what they are selling (one item every 60 seconds) and every 10 seconds they can see how long is left until the next item is broadcasted out.
