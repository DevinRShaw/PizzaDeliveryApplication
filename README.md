# PizzaDeliveryApplication

![Alt text](/diagrams/UpdatedER.drawio.png)

This README outlines the key requirements and data structures for the Pizza Delivery Application.

## 1. User

A user must register to use the application. Each user account will have the following information:

- **phoneNum** *(required)*: Contact number of the user.
- **login** *(required)*: Username for logging into the application.
- **password** *(required)*: Password for account access.
- **role** *(required)*: Role within the application (e.g., Customer, Driver, Manager).
  - **Default Role**: New accounts automatically receive the role "Customer."
  - **Role Management**: Only a Manager can update the role from "Customer" to "Driver" or "Manager."
- **address** *(required)*: Address of the user.
- **favoriteItem**: User's preferred item.

### User Permissions
- **All Users**: 
  - Can view the menu, place orders, check order status, and view/edit profile.
- **Customer Profile Permissions**:
  - Can update all fields in their profile except `login` and `role`.
- **Driver and Manager**:
  - Have additional permission to update delivery information for an order.
- **Manager**:
  - Has permissions to update the menu and modify user roles.

## 2. Item

Items represent the food offerings that customers can order, such as pizzas and drinks. Each item has the following properties:

- **itemName** *(required)*: The name of the item (e.g., "Margherita Pizza").
- **type** *(required)*: The category or type of the item (e.g., pizza, drink).
- **price** *(required)*: Price of the item.
- **ingredients**: List of ingredients in the item.
- **description**: Description of the item.
- **imageURL**: URL of an image representing the item.

### Item Management
- **Only Managers** can update or manage the list of items.

## 3. Order

The primary interaction between customers and the pizza store is through orders. Each order has the following attributes:

- **orderID** *(required)*: Unique identifier for each order.
- **login** *(required)*: Username of the user who placed the order.
- **orderTimestamp** *(required)*: Timestamp when the order was placed.
- **totalPrice** *(required)*: Total cost of the order.
- **orderStatus** *(required)*: Current status of the order.

### Order Process
1. When a user places an order:
   - Record the user’s `login`, the `orderTimestamp`, and the `totalPrice`.
   - Set the `orderStatus` to “Order Received.”
2. A unique `orderID` is then generated for this order.

## 4. Store

Customers place orders at specific store locations. Each store has the following details:

- **storeID** *(required)*: Unique identifier for the store.
- **address** *(required)*: Physical address of the store.
- **city** *(required)*: City where the store is located.
- **state** *(required)*: State where the store is located.
- **isOpen** *(required)*: Boolean indicating if the store is currently open.
- **reviewScore**: Customer rating for the store.

### Order Location
- Each order is linked to a specific store where it was placed.

---

This document serves as a reference for understanding the core entities and permissions within the Pizza Delivery Application.
