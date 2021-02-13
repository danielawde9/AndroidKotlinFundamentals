# Android Collab 

This codelab is part of the Android Kotlin Fundamentals course. All the course codelabs are listed on the Android Kotlin Fundamentals codelabs landing page.

* auto-gen TOC:
{:toc}

# Data binding basics (databinding app)
   
when your app has complex view hierarchies, findViewById() is expensive and slows down the app, because Android traverses the view hierarchy, starting at the root, until it finds the desired view. Fortunately, there's a better way.

Every time you use findViewById() to search for a view after the view is created or recreated, the Android system traverses the view hierarchy at runtime to find it. When your app has only a handful of views, this is not a problem. However, production apps may have dozens of views in a layout, and even with the best design, there will be nested views.

One solution is to create an object that contains a reference to each view. This object, called a Binding object, can be used by your whole app. This technique is called data binding. Once a binding object has been created for your app, you can access the views, and other data, through the binding object, without having to traverse the view hierarchy or search for the data.

![](.readme_images/f3a67115.png)

Data binding has the following benefit

- code is shorter, easier to read and maintian than code uses findviewbyid()
- data and views are clearly seperated. this benefit of data binding becomes increasiongly imporant 
- the android system only traverses the view hierarchy once to get each view, and happen during app startup not at runtime when the user is interacting with the app\
- you get type safety for accessing (meanes that the compiler validates types while compiling and it throws an error if you try to assign the wrong tupe to a varialble)

## Step 1 Enable data binding 

to user data binding you need to enable in gradule file (not enabled by default, cause increases compiling time and may affect app startup time)

Open the build.gradle (Module: app) file.
Inside the android section, before the closing brace, add a buildFeatures section and set dataBinding to true.

    buildFeatures {
        dataBinding true
    }

// it asked me to apply `id 'kotlin-kapt'` in plugins

## Step 2 Changle layout file to be usable with data binding 

you need to wrap the xml with `<layout>` tag, this is so that the root calss is no loginger a view group, but instead a layout that contains view vgroups and views

## Step 3 create a data binding object in the main activity 

add a reference to the binding objet to the main acitivty si that you cans use it to access views, then import it 

    private lateinit var binding: ActivityMainBinding

Next, you replace the current `setContentView()` function with an instruction that does the following:

- creates the binding object
- uses the `setContentView()` function from the DataBindingUtil class to associate the `activity_main` layout with the `MainActivity` 
- in `onCreate()` replace the `setContentView()` call with the following line of code and import it


    binding = DataBindingUtil.setContentView(this,R.layout.activity_main)


## Step 4 use the binding object to replace all calls to the findViewById()

When the binding object is created, the compiler generates the names of the views in the binding object from the IDs of the views in the layout, converting them to camel case
for example the `done_button` is `doneButton` in the binding obejct

    binding.doneButton.setOnClickListener{
        addNickname(it)
    }
    
inside the addNickname

    binding.nicknameText.text = binding.nicknameEdit.text.tostring()
    binding.nicknameEdit.visibility = View.GONE
    binding.doneButton.visibility = View.GONE
    binding.nicknameText.visibility = View.VISIBLE

kotlinze the fiunciotn

    binding.apply {
       nicknameText.text = nicknameEdit.text.toString()
       nicknameEdit.visibility = View.GONE
       doneButton.visibility = View.GONE
       nicknameText.visibility = View.VISIBLE
    }
    
## Step 5 create the model 

    data class MyName(var name: String = "", var nickname: String = "")

the data keyword for all model classes (the class will hold data) also the compiler will create extra classes such as equals() and hashcode() and toString()

// not valid here
the @string Rest annotation is not required but we recommend you include it

- the annotation helps the code inspector built into android studio named Lint verify at compile time that usages of the constructor provide a valid string resource ID
- the annotation makes your code more readble for other developer

## Step 6 add data

in xml the the name Textview will reference to the name from the data class

between the `<layout>` tags add `<data></data>`

    <data>
        <variable
            name="myName"
            type="com.daniel.androidkotlinfundamentals" />
    </data>
    
    inside the textview
    
    android:text="@={myName.name}"
    
    and 
    
    android:text="@={myName.nickname}"

in the main act.

    private val myName: MyName = MyName("Aleks Haecky")

and in on create set the layout value

    binding.myName = myName

for the nickname get the data from the edit text

    myName?.nickname = nicknameEdit.text.toString()

After the nickname is set, you want your code to refresh the UI with the new data. To do this, you must invalidate all binding expressions so that they are recreated with the correct data.

Add `invalidateAll()` after setting the nickname so that the UI is refreshed with the value in the updated binding object.

    binding.apply {
       myName?.nickname = nicknameEdit.text.toString()
       invalidateAll()
       ...
    }

run 

# Navigation
Base code provided form the google collab
