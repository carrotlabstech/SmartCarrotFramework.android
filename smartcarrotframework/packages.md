# Module smartcarrotframework
© 2021 [Carrot Labs GmbH][../../../index.html]. All rights reserved. (Last updated: 2021-03-22)
Version 1.0.1

## On device Personal Finance Manager.

**Package documentation** [com.carrotlabs.smartcarrotframework][com.carrotlabs.smartcarrotframework/index.html]

## Installation
The simplest way to integrate **SmartCarrotFramework** is to use gradle.

a) add following code snippet into `allprojects` in the **top-level** `build.gradle`:


```
allprojects {
    repositories {

       # Add this block before google or/and jcenter Maven repositories
       # --------------------------------------------------------------
        maven {
            url "https://gitlab.com/api/v4/projects/24153564/packages/maven"
            name "GitLab"

            credentials(HttpHeaderCredentials) {
                name = "Private-Token"
                value = "YOUR_TOKEN_HERE"   # replace the token with your token
            }
            authentication {
                header(HttpHeaderAuthentication)
            }
        }

        # --------------------------------------------------------------
        # end of the block

        google()
        jcenter()
    }
}
```

b) replace the **YOUR_TOKEN_HERE** value of the `Private-Token` with your own (you can generate it as GitLab suggests [here:](https://docs.gitlab.com/ee/user/profile/personal_access_tokens.html)

Allow `read_registry` scope at least.


c) extend `dependencies` section in the **application level** `build.gradle` as per your license:

```
implementation 'com.carrotlabs:smartcarrotframework.us:1.0.1'
```
or
```
implementation 'com.carrotlabs:smartcarrotframework.ch:1.0.1'
```

d) Done

## Manual installation
Please follow these steps to install the library:

a) Make sure you have added these lines into the **build.gradle**:
```
repositories {
        flatDir {
            dirs 'libs'
        }
    }
```
b) Add dependencies:
- [ThreeTenABP](https://github.com/JakeWharton/ThreeTenABP). We use this library to make it easier to work with Date and Time in the old Java versions. Please find instruction on its downloading [here](https://github.com/JakeWharton/ThreeTenABP#download)
- [Kotlin](https://kotlinlang.org/) stdlib library. If you already use Kotlin in your project then you don't need to do anything. Otherwise please add following line to your **application/build.gradle** file:
```
implementation "org.jetbrains.kotlin:kotlin-stdlib:1.4.0"
```
- **TensorFlowLight**. **DON'T** use the standard Maven library and use the shared one. Please copy it to the `libs` folder and add the following instruction to the **application/build.gradle**:
```
implementation files('libs/tensorflow-lite.aar')
```
- **SmartCarrotFramework**. Copy it to the `libs` folder and add the following instruction to the **application/build.gradle**:
```
implementation files('libs/smartcarrotframework.aar')
```
c) Done.

## Licensing
Licenses are granted on per module basis. You share your application bundle id with **Carrot Labs**, and we will send you a mail with the license and instructions.

How to set the license:

```kotlin
import com.carrotlabs.smartcarrotframework

...

fun setLicense(license: String) {
    val carrotContext = CarrotContext()

    try {
        carrotContext.setLicense(license = license, appContext = _applicationContext)
    } catch (e: Exception) {
        ...
    }
}
```

## Modules
**SmartCarrotFramework** consists of few modules:
- [CategorisationAPI] to categorise transactions.
- [TransactionAPI] to categorise, manipulate, and store transactions.
- [BudgetAPI] to set incremental budgets on transactions and be able to receive alerts if a budget did overspent, or did reach its spend threshold.
- [ZenAPI] to let the end user carefully look after own financial health and form positive habits.

The full power of the **SmartCarrotFramework** can be obtained only when all the building blocks are used jointly.

## Categorisation
It is very easy to categorise a transaction: use a `CategorisationAPI` object for that:

```kotlin
import com.carrotlabs.smartcarrotframework

...

fun doCategorise(transactionDescription: String, transactionAmount: BigDecimal) {
    try {
        val categorisationAPI = carrotContext.getCategorisationAPI()

        val categoryId = categorisationAPI.categorise(description = transactionDescription, amount = transactionAmount)
    } catch (e: Exception) {
        ...
    }
}
```

## Transaction Categorisation
You don't need to categorise transactions in a separate method call. At the time of [TransactionAPI.upsert] transactions do categorise automatically.

## Budgets
* [BudgetFrequency.monthly] budget takes into account all the transactions whose `Transaction.bookingDate` belongs to the **current month**.
* [BudgetFrequency.annual] budget takes into account all the transactions whose `Transaction.bookingDate` belongs to the **current year**.
* [BudgetAPI.getRunningTotal] is always positive, it is a sum of all the transactions per budget. If a budget's category is a top level category than transactions of the category's sub categories are not added. Budget of the [TransactionCategory.UNCATEGORISED] category is allowed.
* [BudgetAPI.getBalance] is **positive** if budget's running total is less than the budgeted amount and is **negative** otherwise.

When a budget gets saved:
* its [Budget.id] is trimmed
* Negative [Budget.budgeted] amount is set to **0**
* Negative [Budget.alert] amount is set to **0**
* Negative [Budget.categoryId] is set to [TransactionCategory.UNCATEGORISED_INT_ID]


## Persistence
All entities are encrypted and stored in the **SQLite**. The encryption key should be provided by the customer and set right after the license validation:

```swift
import com.carrotlabs.smartcarrotframework

...

fun setLicense(license: String) {
    var carrotContext = CarrotContext()

    try {
        carrotContext.setLicense(license = license, appContext = _applicationContext)
        carrotContext.setEncryption(key = "thisIsGreatKey!", iv = "1234567890123456")
    } catch (e:Exception) {
        ...
    }
}
```

the encryption key consists of:
* the **key** by itself,
* **initialisation vector**, **must be of 16 characters long.**

## Entity Ids
The Framework is not responsible for entity ([Transaction], [Budget]) unique identifiers. They should be maintained by the customer. The ids are of type [String] and the customer can use either own backend entity ids or [UUID] values.


If an entity id is empty, the entity can't be saved into the persistent storage and a respective `EmptyEntityIdCarrotContextError` is thrown.

Entity Ids are trimmed before being saved.

## Events
At the moment these four types of events are emitted:
* [NotificationCenter.NotificationName.transactionListDidUpdate] if a transaction in the persistent storage got updated, inserted, deleted or categorised.
* [NotificationCenter.NotificationName.budgetListDidUpdate] if a budget in the persistent storage got updated, inserted or deleted.
* [NotificationCenter.NotificationName.budgetDidAlert] if a budget's [runningTotal][BudgetAPI.getRunningTotal] exceeded its [alert][Budget.alert] value but still less than its [budgeted][Budget.budgeted] value.
* [NotificationCenter.NotificationName.budgetDidOverSpend] if the budget's [runningTotal][BudgetAPI.getRunningTotal] exceeded its [budgeted][Budget.budgeted] value.

How to subscribe to events:

```kotlin
import com.carrotlabs.smartcarrotframework

...
// Observer
class Observer : Notifiable {
    override fun onNotification(notify: Notification?) {
        // respond to the event
    }

    // Subscribe
    fun subscribeToBudgetNotification() {
        NotificationCenter.addObserver(NotificationCenter.NotificationName.budgetDidAlert, this)
    }
}
```

## Zen Score
Zen Score represents a user's financial health score. It falls in range (0; 1) and a higher value means better financial health.

Zen Scores are calculated for a consequent range of dates (days), the maximum time period is defined by [ZenParams.MAX_INTERVAL]. If the requested time interval exceeds the maximum time interval than the scores will be calculated only for the number of days defined by [ZenParams.MAX_INTERVAL]. The **closest** date of the requested time interval will be included into the result set.

Zen Scores require at least one `Budget` to be created, otherwise [ZenAPI] will throw an error.


