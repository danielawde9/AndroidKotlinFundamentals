# Android Collab 

This codelab is part of the Android Kotlin Fundamentals course. All the course codelabs are listed on the Android Kotlin Fundamentals codelabs landing page.

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
    
inside the Name textview
    
    android:text="@={myName.name}"
    
to add a default text and value

    tools:text="What color is the Android mascot?"
    
    android:text="@={myName.nickname}"

in the main act we add data using the model

    private val myName: MyName = MyName("Aleks Haecky")

and in on create bind the layout variable to the activity class

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

### Full code

```kotlin
class MainActivity : AppCompatActivity() {
    data class MyName(var name: String = "", var nickname: String = "")
    
    private lateinit var binding: ActivityMainBinding
    private val myName: MyName = MyName("Aleks Haecky")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.myName = myName

        binding.doneButton.setOnClickListener{
            addNickName(it)
        }
    }

    private fun addNickName(view: View) {
        myName?.nickname = binding.nicknameEdit.text.toString()
        binding.invalidateAll()
        binding.nicknameText.text = binding.nicknameEdit.text.toString()
        binding.nicknameEdit.visibility = View.GONE
        binding.doneButton.visibility = View.GONE
        binding.nicknameText.visibility = View.VISIBLE
    }
}
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:tools="http://schemas.android.com/tools"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <data>
        <variable
            name="myName"
            type="com.daniel.androidkotlinfundamentals.MyName" />
    </data>

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".MainActivity">

        <TextView
            android:id="@+id/name_text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginBottom="64dp"
            android:text="@={myName.name}"
            app:layout_constraintBottom_toTopOf="@+id/nickname_edit"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintHorizontal_bias="0.5"
            app:layout_constraintStart_toStartOf="parent" />

        <TextView
            android:id="@+id/nickname_text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@={myName.nickname}"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <EditText
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="56dp"
            android:id="@+id/nickname_edit"
            app:layout_constraintBottom_toTopOf="@+id/nickname_text"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintHorizontal_bias="0.0"
            app:layout_constraintStart_toStartOf="parent" />

        <com.google.android.material.button.MaterialButton
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="3i3i"
            android:id="@+id/done_button"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@+id/nickname_text" />
    </androidx.constraintlayout.widget.ConstraintLayout>
</layout>

```

# NavigationApp

Base code provided form the google collab

in fragment binding the layout to the fragment and inflate it using (in the onCreateView())

    // inflate the layout
    val binding = DataBindingUtil.inflate<FragmentGameBinding>(
        inflater, R.layout.fragment_game, container, false
    )
    
    //bind this fragment class to the layout (data variable)
    binding.game = this
    
    return binding.root
    
-inflater, which is the LayoutInflater used to inflate the binding layout.
-The XML layout resource of the layout to inflate. Use one of the layouts that is already defined for you, R.layout.fragment_title.
-container for the parent ViewGroup. (This parameter is optional.)
-false for the attachToParent value.
-Assign the binding that DataBindingUtil.inflate returns to the binding variable.
-Return binding.root from the method, which contains the inflated view. Your onCreateView() method now looks like the following code:

### Side note

in activity
   
    binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

    binding.myName = myName
    
in radio group 
    
    val checkedId = binding.questionRadioGroup.checkedRadioButtonId
    // Do nothing if nothing is checked (id == -1)
    if (-1 != checkedId) {


## Navigation Component
is a lib that can manage complex naviagation deeplinking and compile time 

in project build.gradle 

    ext {
            navigationVersion = "2.3.0"
            kotlin_version = "1.4.30"
        }

in app build gradle
    
    dependencies {
      ...
      implementation "androidx.navigation:navigation-fragment-ktx:$navigationVersion"
      implementation "androidx.navigation:navigation-ui-ktx:$navigationVersion"
      ...
    }
    
### Adding a navigation graph to the project
In the Project: Android pane, right-click the res folder and select New > Android Resource File.
In the New Resource File dialog, select Navigation as the Resource type.
In the File name field, name the file navigation.
Make sure the Chosen qualifiers box is empty, and click OK. A new file, navigation.xml, appears in the res > navigation folder.
Open the res > navigation > navigation.xml file and click the Design tab to open the Navigation Editor. Notice the No NavHostFragments found message in the layout editor


### NavHostFragment
a navigation host fragment acts as a host for the framgnets ina a navigation graph. The naviagation host fragment ius usually named NavHostFragment

As the user moves between destinations defined in the navigation graph, the navigation host Fragment swaps fragments in and out as necessary. The Fragment also creates and manages the appropriate Fragment back stack.

in the `activity_main.xml` add `androidx.navigation.fragment.NavHostFragment`
-add ID
-The navigation host Fragment needs to know which navigation graph resource to use. Add the app:navGraph attribute and set it to the navigation graph resource, which is @navigation/navigation.
-Add the app:defaultNavHost attribute and set it to "true". Now this navigation host is the default host and will intercept the system Back button.
     
       <fragment
            android:id="@+id/myNavHostFragment"
            android:name="androidx.navigation.fragment.NavHostFragment"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:navGraph="@navigation/navigation"
            app:defaultNavHost="true" />


Open navigation.xml from the navigation resource folder. In the Navigation Editor, click the New

![](.readme_images/a2822ec1.png)

Select fragment_title. You add fragment_title first because the TitleFragment Fragment is where app

If the preview shows a "Preview Unavailable" message, click the Code tab to open the navigation XML. Make sure that the fragment element for the gameFragment includes tools:layout="@layout/fragment_game", as shown below.
    
    <fragment
       android:id="@+id/gameFragment"
       android:name="com.example.android.navigation.GameFragment"
       android:label="GameFragment"
       tools:layout="@layout/fragment_game" />

In the layout editor (using the Design view), drag the game Fragment to the right so it doesn't overlap with the title Fragment.

n the preview, hold the pointer over the title Fragment. A circular connection point appears on the right side of the Fragment view. Click the connection point and drag it to the gameFragment or drag it to anywhere in the gameFragment preview. An Action is created that connects the two fragments.
To see the Action's attributes, click the arrow that connects the two fragments. In the Attributes pane, check that the action's ID is set to action_titleFragment_to_gameFragment.

![](.readme_images/3a29f95c.png)

### Add a click handler to the Play button

In Android Studio, open the TitleFragment.kt file. Inside the onCreateView() method, add the following code before the return statement:

    binding.playButton.setOnClickListener { view : View ->
           view.findNavController().navigate(R.id.action_titleFragment_to_gameFragment)
    }
    
in **fragment** 

    // We've won!  Navigate to the gameWonFragment.
    view.findNavController()
       .navigate(R.id.action_gameFragment_to_gameWonFragment)
    
### Set the pop behavior for the navigation actions
   
The Android system's Back button is shown as 1 in the screenshot above. If the user presses the Back button in the game-won fragment or the game-over Fragment, the app navigates to the question screen. Ideally, the Back button should navigate back to the app's title screen. You change the destination for the Back button in the next task.

Until now, you've let the navigation controller handle the back stack for you. When the user navigates to a destination in your app, Android adds this destination to the back stack.

In this step, you manage the back stack so that when the user is at the GameWon or GameOver screen, pressing the Back button returns them to the title screen. You manage the back stack by setting the "pop" behavior for the actions that connect the fragments:

- the `popUpTo` attribute of an action " pops up" the back stack to a given destination before navigation (destinations are removed from the backstack)
- if the `popUpToInclusive` attribute is false or is not set, `popUpTo` removes destinations up ot specifed destination but leaves the specified desticnation in the back stac
- if `popUpToInclusive` is set to `true` the popUpTo attribute remives all destinations up to and including the back stact
- If `popUpToInclusive` is true and popUpTo is set to the app's starting location, the action removes all app destinations from the back stack. The Back button takes the user all the way out of the app

in this set you set the popUpTo for the 2 action using Pop To 

* Select the action for navigating from the gameFragment to the gameOverFragment. (In the preview area, the action is represented by a blue line that connects the two fragments.)
* In the Attributes pane, set popUpTo to gameFragment. Select the popUpToInclusive checkbox.

![](.readme_images/b6a2e150.png)

This sets the popUpTo and popUpToInclusive attributes in the XML. The attributes tell the navigation component to remove fragments from the back stack up to and including GameFragment. (This has the same effect as setting the popUpTo field to titleFragment and clearing the popUpToInclusive checkbox.)

and again for the gameWonFragment

to create a user flow when trying again in gameOverFragment create a flow to gameFragment

in the attrbutes pane set the actions popUpTo to title fragment, and clear the popUpToInclusive checkbox, because you do not want the titleFragment to be included in the destination that are removed, 
we want everything up to the titleFragment (but not including it) to be removed from the back stack
same for the gameWon

#### Up Button in the navbar

Open the MainActivity.kt kotlin file. Inside the onCreate() method, add code to find the navigation controller object:

    val navController = this.findNavController(R.id.myNavHostFragment)
Also inside the onCreate() method, add code to link the navigation controller to the app bar:

    NavigationUI.setupActionBarWithNavController(this,navController)
After the onCreate() method, override the onSupportNavigateUp() method to call navigateUp() in the navigation controller:

    override fun onSupportNavigateUp(): Boolean {
            val navController = this.findNavController(R.id.myNavHostFragment)
            return navController.navigateUp()
        }

#### option menu
in navigation add the about fragment.

Add the options-menu resource
In the Android Studio Project pane, right-click the res folder and select New > Android Resource File.
In the New Resource File dialog, name the file options_menu.
Select Menu as the Resource type and click OK


Open the options_menu.xml file from the res > menu folder and click the Design tab to see the Layout Editor.
From the Palette pane, drag a Menu Item (shown as 1 in the screenshot below) and drop it anywhere in the design editor pane (2). A menu item appears in the preview (3) and in the Component Tree (4).

>Tip: Make sure that the ID of the menu item that you just added is exactly the same as the ID of the AboutFragment that you added in the navigation graph. This will make the code for the onClick handler much simpler.

Open the TitleFragment.kt Kotlin file. Inside the onCreateView() method, before the return, call the setHasOptionsMenu() method and pass in true.
After the onCreateView() method, override the onCreateOptionsMenu() method. In the method, add the options menu and inflate the menu resource file.

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            super.onCreateOptionsMenu(menu, inflater)
            inflater.inflate(R.menu.options_menu, menu)
    }
    
Override the onOptionsItemSelected() method to take the appropriate action when the menu item is tapped. In this case, the action is to navigate to the Fragment that has the same id as the selected menu item.
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         return NavigationUI.
                onNavDestinationSelected(item,requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

### Navigation Drawer
    
To create a navigation drawer, you create the navigation menu. You also need to put your views inside a DrawerLayout in the layout file.

>Note: If you use the same ID for the menu item as for the destination Fragment, you don't need to write any code at all to implement the onClick listener!

Wrap the entire <LinearLayout> inside a <DrawerLayout>. in main activity xml

    <layout xmlns:android="http://schemas.android.com/apk/res/android"
       xmlns:app="http://schemas.android.com/apk/res-auto">
       <androidx.drawerlayout.widget.DrawerLayout
           android:id="@+id/drawerLayout"
           android:layout_width="match_parent"
           android:layout_height="match_parent">
    
       <LinearLayout
           . . . 
           </LinearLayout>
       </androidx.drawerlayout.widget.DrawerLayout>
    </layout>

Now add the drawer, which is a NavigationView that uses the navdrawer_menu that you just defined. Add the following code in the DrawerLayout, after the </LinearLayout> element:

    <com.google.android.material.navigation.NavigationView
       android:id="@+id/navView"
       android:layout_width="wrap_content"
       android:layout_height="match_parent"
       android:layout_gravity="start"
       app:headerLayout="@layout/nav_header"
       app:menu="@menu/navdrawer_menu" />
       
Display the navigation drawer
You created the menu items for the navigation drawer and the navigation drawer layout. Now you need to connect the navigation drawer to the navigation controller so that when users select items in the navigation drawer, the app navigates to the appropriate Fragment.
Open the Mainactivity.kt Kotlin file. In onCreate(), add the code that allows the user to display the navigation drawer. Do this by calling setupWithNavController(). Add the following code at the bottom of onCreate():
    
    NavigationUI.setupWithNavController(binding.navView, navController)

    
Display the navigation drawer from the drawer button
The final step is to enable the user to access the navigation drawer from the drawer button at the top left of the app bar.

In the Mainactivity.kt Kotlin file, add the lateinit drawerLayout member variable to represent the drawer layout:

    private lateinit var drawerLayout: DrawerLayout

Inside the onCreate() method, initialize drawerLayout after the binding variable has been initialized.

    val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,
                    R.layout.activity_main)
    
    drawerLayout = binding.drawerLayout
Add the drawerLayout as the third parameter to the setupActionBarWithNavController() method:
    
    NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
Edit the onSupportNavigateUp() method to return NavigationUI.navigateUp instead of returning navController.navigateUp. Pass the navigation controller and the drawer layout to navigateUp(). The method will look like as follows:

    override fun onSupportNavigateUp(): Boolean {
       val navController = this.findNavController(R.id.myNavHostFragment)
       return NavigationUI.navigateUp(navController, drawerLayout)
    }
    
## Summary
To use the Android navigation library, you need to do some setup:

- Add dependencies for navigation-fragment-ktx and navigation-ui-ktx in the module-level build.gradle file.
- Add an ext variable for the navigationVersion in the project-level build.gradle file.

Navigation destinations are fragments, activities, or other app components that the user navigates to. A navigation graph defines the possible paths from one navigation destination to the next.

-To create a navigation graph, create a new Android resource file of type Navigation. This file defines the navigation flow through the app. The file is in the res/navigation folder, and it's typically called navigation.xml.
-To see the navigation graph in the Navigation Editor, open the navigation.xml file and click the Design tab.
-Use the Navigation Editor to add destinations such as fragments to the navigation graph.
-To define the path from one destination to another, use the Navigation Graph to create an action that connects the destinations. In the navigation.xml file, each of these connections is represented as an action that has an ID.

a navigation host fragment usually named NavHostFragment acts as a host for framgents in the navigation graph

- as the user moves between destinations deffined in the navgation graph the `navhostfrgament`swaps the the fragments in and out oand manages the fragment backstack
- in the activity_main.xml layout file the `NavHostFragment` is represented be a fragment element with name `android:name="androidx.navigation.fragment.NavHostFragment"`

to define which fragment is displayed when the user taps a view for example a buitton. set on the onClick listener for the view call

    findNavController().navigate()
    
specify the id of the action

Conditional navigation navigates to one screen in one case, and to a different screen in another case. to create conditional navigation:

- use the navigation editor to create a connection from the starting fragment to each of the possible destination fragments
- give each connection a unique ID
- in the click listener method for the View, add code to detect the condition then call the findNavController().navigate() on the view, passing in the ID for the appropriate action.


The back button

- in the navigation editor, you can use the attributes pane to change the action's `popUpTo` setting. this setting remives destinations from the back stack whihc have the effect of determining where the back button takes the user
- the `popUpTo` setting apprears as the `popUpTo` attribute in the `navigation.xml`
- selecting the `popUpToInclusive` checkbox sets the attribute to true > all destinations up to and including this destination are removed form the backstack
![](.readme_images/1e366b04.png)

if the action popUpTo attrubte is set to apps starting destination and inclusive is set to tryuue the backbutton takes the user out of the app

The Up Button
The navigation controller's NavigationUI library integrates with the app bar to allow the user to tap the Up button on the app bar to get back to the app's home screen from anywhere in the app.

To link the navigation controller to the app bar:

    val navController = this.findNavController(R.id.myNavHostFragment)
    NavigationUI.setupActionBarWithNavController(this,navController)

Override the onSupportNavigateUp() method to call navigateUp() in the navigation controller:

    override fun onSupportNavigateUp(): Boolean {
            val navController = this.findNavController(R.id.myNavHostFragment)
            return navController.navigateUp()
        }
    }

The options menu

The options menu is a menu that the user accesses from the app bar by tapping the icon with the three vertical dots 4cdd17fa43bfbe6.png. To create an options menu with a menu item that displays a Fragment, make sure the Fragment has an ID. Then define the options menu and code the onOptionsItemSelected() handler for the menu items.

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            super.onCreateOptionsMenu(menu, inflater)
            inflater.inflate(R.menu.options_menu, menu)
    }

Override the onOptionsItemSelected() method to take the appropriate action when the menu item is clicked. The following code displays the Fragment that has the same ID as the menu item. (This code only works if the menu item and the Fragment have identical ID values.)


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         return NavigationUI.
                onNavDestinationSelected(item,requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

Navigation drawer

Add dependencies to build.gradle:
The navigation drawer is part of the Material Components for Android library. Add the Material library to the build.gradle (app) file:


    dependencies {
        ...
        implementation "com.google.android.material:material:$supportlibVersion"
        ...
    }

Give each destination Fragment an ID:
If a Fragment is reachable from the navigation drawer, open it in the navigation graph to make sure that it has an ID.
Create the menu for the drawer:
Create an Android resource file of type Menu (typically called navdrawer_menu) for a navigation drawer menu. This creates a new navdrawer_menu.xml file in the Res > Menu folder.
In the design editor, add Menu Item widgets to the Menu.
Add the drawer to the layout for the Fragment:
In the layout that contains the navigation host Fragment (which is typically the main layout), use <androidx.drawerlayout.widget.DrawerLayout> as the root view.
Add a <com.google.android.material.navigation.NavigationView> view to the layout.
Connect the drawer to the navigation controller:
Open the Activity that creates the navigation controller. (The main Activity is typically the one you want here.) In onCreate(), use NavigationUI.setupWithNavController()to connect the navigation drawer with the navigation controller:

    val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
           this, R.layout.activity_main)
    NavigationUI.setupWithNavController(binding.navView, navController)

Set up the drawer button in the app bar:
In onCreate() in the Activity that creates the navigation controller (which is typically the main Activity), pass the drawer layout as the third parameter to NavigationUI.setupActionBarWithNavController
val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
    this, R.layout.activity_main)

    NavigationUI.setupActionBarWithNavController(
        this, navController, binding.drawerLayout)
To make the Up button work with the drawer button, edit onSupportNavigateUp() to return NavigationUI.navigateUp(). Pass the navigation controller and the drawer layout to navigateUp().

    override fun onSupportNavigateUp(): Boolean {
       val navController = this.findNavController(R.id.myNavHostFragment)
       return NavigationUI.navigateUp(navController, drawerLayout)
    }

# External Activities

Before users can share their game results from within the AndroidTrivia app, your code needs to pass parameters from one Fragment to another. To prevent bugs in these transactions and make them type-safe, you use a Gradle plugin called Safe Args. The plugin generates NavDirection classes, and you add these classes to your code.

Your app could use a Bundle to pass data from Fragment A to Fragment B. For example, Fragment A creates a bundle and saves the information as key-value pairs, then passes the Bundle to Fragment B. Then Fragment B uses a key to fetch a key-value pair from the Bundle. This technique works, but it can result in code that compiles, but then has the potential to cause errors when the app runs.

The kinds of errors that can occur are:

- Type mismatch errors. For example, if Fragment A sends a string but Fragment B requests an integer from the bundle, the request returns the default value of zero. Since zero is a valid value, this kind of type mismatch problem does not throw an error when the app is compiled. However, when the user runs the app, the error might make the app misbehave or crash.
- Missing key errors. If Fragment B requests an argument that isn't set in the bundle, the operation returns null. Again, this doesn't throw an error when the app is compiled but could cause severe problems when the user runs the app.
You want to catch these errors when you compile the app in Android Studio, so that you catch these errors before deploying the app into production. In other words, you want to catch the errors during app development so that your users don't encounter them.
 
## Safe Args dependencies  
    
    // Adding the safe-args dependency to the project Gradle file
    dependencies {
       ...
    classpath "androidx.navigation:navigation-safe-args-gradle-plugin:$navigationVersion"
    
    }

Open the app-level build.gradle file.
At the top of the file, after all the other plugins, add the apply plugin statement with the androidx.navigation.safeargs plugin:

    apply plugin: 'androidx.navigation.safeargs'

The app project now includes generated NavDirection classes.

The Safe Args plugin generates a NavDirection class for each Fragment. These classes represent navigation from all the app's actions.

For example, GameFragment now has a generated GameFragmentDirections class. You use the GameFragmentDirections class to pass type-safe arguments between the game Fragment and other fragments in the app.

To see the generated files, explore the generatedJava folder in the Project > Android pane.

>Caution: Do not edit the NavDirection classes. These classes are regenerated whenever the project is compiled, and your edits will be lost

Add a NavDirection class to the game Fragment

Open the GameFragment.kt Kotlin file that's in the java folder.
Inside the onCreateView() method, locate the game-won conditional statement ("We've won!"). Change the parameter that's passed into the NavController.navigate() method: Replace the action ID for the game-won state with an ID that uses the actionGameFragmentToGameWonFragment() method from the GameFragmentDirections class.

### add the code

    view.findNavController()
            .navigate(GameFragmentDirections.actionGameFragmentToGameOverFragment())
            

// Using directions to navigate to the GameWonFragment
    
    view.findNavController()
        .navigate(GameFragmentDirections.actionGameFragmentToGameWonFragment())

we add arguments to the gameWonFragment and pass the argumnets safelly into a GameFragmentDirections method. similarly you then will replace the other Fragment classes with their equivialent NavDirection classes

### add the arguments
Open the navigation.xml file, which is in the res > navigation folder. Click the Design tab to open the navigation graph, which is where you'll set the arguments in the fragments.
In the preview, select the `gameWonFragment`.
In the Attributes pane, expand the Arguments section.
Click the + icon to add an argument. Name the argument numQuestions and set the type to Integer, then click Add. This argument represents the number of questions the user answered.

If you try to build the app now, you will likely get two compile errors.


No value passed for parameter 'numQuestions'
No value passed for parameter 'numCorrect'

to pass 

    // Adding the parameters to the Action
    view.findNavController()
          .navigate(GameFragmentDirections
                .actionGameFragmentToGameWonFragment(numQuestions, questionIndex))

to receive in gamewontfragment

    val args = GameWonFragmentArgs.fromBundle(requireArguments())
    Toast.makeText(context, "NumCorrect: ${args.numCorrect}, NumQuestions: ${args.numQuestions}", Toast.LENGTH_LONG).show()

### Replace Fragment classes with NavDirection classes

When you use "safe arguments," you can replace Fragment classes that are used in navigation code with NavDirection classes. You do this so that you can use type-safe arguments with other fragments in the app.

in TitleFragment

previously
            
                view.findNavController().navigate(R.id.action_titleFragment_to_gameFragment)

now 

                view.findNavController().navigate(TitleFragmentDirections.actionTitleFragmentToGameFragment())

same for GameOverFragment.kt 

        .navigate(GameOverFragmentDirections.actionGameOverFragmentToGameFragment())


## Implicit intents
An Intent is a simple message object that's used to communicate between Android components. There are two types of intents: explicit and implicit. You can send a message to a specific target using an explicit intent. With an implicit intent, you initiate an Activity without knowing which app or Activity will handle the task. For example, if you want your app to take a photo, you typically don't care which app or Activity performs the task. When multiple Android apps can handle the same implicit intent, Android shows the user a chooser, so that the user can select an app to handle the request.

Each implicit intent must have an ACTION that describes the type of thing that is to be done. Common actions, such as ACTION_VIEW, ACTION_EDIT, and ACTION_DIAL, are defined in the Intent class.

Add an options menu to the Congratulations screen
Open the GameWonFragment.kt Kotlin file.
Inside the onCreateView() method, before the return, call the setHasOptionsMenu() method and pass in true:

    setHasOptionsMenu(true)

### Build and call an implicit intent
Modify your code to build and call an Intent that sends the message about the user's game data. Because several different apps can handle an ACTION_SEND intent, the user will see a chooser that lets them select how they want to send their information.

    // Creating our Share Intent
    private fun getShareIntent() : Intent {
       val args = GameWonFragmentArgs.fromBundle(requireArguments())
       val shareIntent = Intent(Intent.ACTION_SEND)
       shareIntent.setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_success_text, args.numCorrect, args.numQuestions))
       return shareIntent
    }
    
    // Starting an Activity with our new Intent
    private fun shareSuccess() {
       startActivity(getShareIntent())
    }
    
    
    // Showing the Share Menu Item Dynamically
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
           super.onCreateOptionsMenu(menu, inflater)
           inflater.inflate(R.menu.winner_menu, menu)
           if(getShareIntent().resolveActivity(requireActivity().packageManager)==null){
                menu.findItem(R.id.share).isVisible = false
           }
    }

 If the result equals null, which means that the shareIntent doesn't resolve, find the sharing menu item from the inflated menu and make the menu item invisible.
 
     // Sharing from the Menu
     override fun onOptionsItemSelected(item: MenuItem): Boolean {
             when(item.itemId){
                 R.id.share -> shareSuccess()
             }
             return super.onOptionsItemSelected(item)
     }

## Summary

Safe args:

- to help catch error by missing or mismatched types whenj u pass data from 1 frag to another
- each frag the safe arg plugin generate a corresponding `navdirection` class. you add the `navdirection` to the fragmnet code 
- the `NavDirection` classes represent navigation from all the app's actions

add the plugins 

in the fragments we can use 

    view.findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameOverFragment())

previously 

    view.findNavController().navigate(R.id.action_titleFragment_to_gameFragment)
        
passing data between fragments using safe args

Open the navigation.xml file, which is in the res > navigation folder. Click the Design tab to open the navigation graph, which is where you'll set the arguments in the fragments.
In the preview, select the `gameWonFragment`.
In the Attributes pane, expand the Arguments section.
Click the + icon to add an argument. Name the argument numQuestions and set the type to Integer, then click Add. This argument represents the number of questions the user answered.

If you try to build the app now, you will likely get two compile errors.


No value passed for parameter 'numQuestions'
No value passed for parameter 'numCorrect'

to pass 

    // Adding the parameters to the Action
    view.findNavController()
          .navigate(GameFragmentDirections
                .actionGameFragmentToGameWonFragment(numQuestions, questionIndex))

to receive in gamewontfragment

    val args = GameWonFragmentArgs.fromBundle(requireArguments())
    Toast.makeText(context, "NumCorrect: ${args.numCorrect}, NumQuestions: ${args.numQuestions}", Toast.LENGTH_LONG).show()


Implicient intent
An implicit intent declares an action that your app wants some other app (such as a camera app or email app) to perform on its behalf.
If several Android apps could handle an implicit intent, Android shows the user a chooser. For example, when the user taps the share icon in the AndroidTrivia app, the user can select which app they want to use to share their game results.
To build an intent, you declare an action to perform, for example ACTION_SEND.
Several Intent() constructors are available to help you build intents.

Sharing functionality:

In the case of sharing your success with your friends, the Intent action would be Intent.ACTION_SEND.
To add an options menu to a Fragment, set the setHasOptionsMenu() method to true in the Fragment code.
In the Fragment code, override the onCreateOptionsMenu() method to inflate the menu.
Override the onOptionsItemSelected() to use startActivity() to send the Intent to other apps that can handle it.
When the user taps the menu item, the intent is fired, and the user sees a chooser for the SEND action.

# Lifecycles and logging

In this codelab, you learn about a fundamental part of Android: the activity and fragment lifecycle. The activity lifecycle is the set of states an activity can be in during its lifetime. The lifecycle extends from when the activity is initially created to when it is destroyed and the system reclaims that activity's resources. As a user navigates between activities in your app (and into and out of your app), those activities each transition between different states in the activity lifecycle.


![](.readme_images/ee209a79.png)
Often, you want to change some behavior, or run some code when the activity lifecycle state changes. Therefore the Activity class itself, and any subclasses of Activity such as AppCompatActivity, implement a set of lifecycle callback methods. Android invokes these callbacks when the activity moves from one state to another, and you can override those methods in your own activities to perform tasks in response to those lifecycle state changes. The following diagram shows the lifecycle states along with the available overridable callbacks.


![](.readme_images/6e9d4543.png)

A fragment also has a lifecycle. A fragment's lifecycle is similar to an activity's lifecycle, so a lot of what you learn applies to both
![](.readme_images/2873b932.png)

## Recap

binding


    // Contains all the views
    private lateinit var binding: ActivityMainBinding
    
in on create()

    // Use Data Binding to get reference to the views
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

    binding.dessertButton.setOnClickListener {
        onDessertClicked()
    }
    
in xml

    
    <layout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        xmlns:tools="http://schemas.android.com/tools">

    <data>

        <variable
            name="revenue"
            type="Integer" />

        <variable
            name="amountSold"
            type="Integer" />
    </data>
    
    ...    
    android:text="@{amountSold.toString()}"

>Note: The onCreate() method is an override. If you override any lifecycle methods, you must immediately call super.onCreate().

Adding timber for debugging

    implementation 'com.jakewharton.timber:timber:4.7.1'

In this step, you create an Application class. Application is a base class that contains global application state for your entire app. It's also the main object that the operating system uses to interact with your app. There is a default Application class that Android uses if you don't specify one, so there's always an Application object created for your app, without you needing to do anything special to create it.

Timber uses the Application class because the whole app will be using this logging library, and the library needs to be initialized once, before everything else is set up. In cases like this, you can subclass the Application class and override the defaults with your own custom implementation.

>Warning: It might be tempting to add your own code to the Application class, because the class is created before all of your activities and can hold global state. But just as it's error-prone to make readable and writable static variables that are globally available, it's easy to abuse the Application class. Avoid putting any activity code in the Application class unless the code is really needed.
    
    override fun onResume() {
       super.onResume()
       Timber.i("onResume Called")
    }

When your app is in the background, it should not be actively running, to preserve system resources and battery life. You use the Activity lifecycle and its callbacks to know when your app is moving to the background so that you can pause any ongoing operations. Then you restart those operations when your app comes into the foreground.


## fragmnet lifecycle
    
    override fun onAttach(context: Context) {
       super.onAttach(context)
       Log.i("TitleFragment", "onAttach called")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       Log.i("TitleFragment", "onCreate called")
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("TitleFragment", "onViewCreated called")
    }
    
    override fun onStart() {
       super.onStart()
       Log.i("TitleFragment", "onStart called")
    }
    override fun onResume() {
       super.onResume()
       Log.i("TitleFragment", "onResume called")
    }
    override fun onPause() {
       super.onPause()
       Log.i("TitleFragment", "onPause called")
    }
    override fun onStop() {
       super.onStop()
       Log.i("TitleFragment", "onStop called")
    }
    override fun onDestroyView() {
       super.onDestroyView()
       Log.i("TitleFragment", "onDestroyView called")
    }
    override fun onDetach() {
       super.onDetach()
       Log.i("TitleFragment", "onDetach called")
    }


Here you can see the entire startup lifecycle of the fragment, including these callbacks:

- onAttach(): Called when the fragment is associated with its owner activity.
- onCreate(): Similarly to onCreate() for the activity, onCreate() for the fragment is called to do initial fragment creation (other than layout).
- onCreateView(): Called to inflate the fragment's layout.
- onViewCreated(): Called immediately after onCreateView() has returned, but before any saved state has been restored into the view.
- onStart(): Called when the fragment becomes visible; parallel to the activity's onStart().
- onResume(): Called when the fragment gains the user focus; parallel to the activity's onResume().
- onPause(): Called when the fragment loses the user focus; parallel to the activity's onPause().
- onStop(): Called when the fragment is no longer visible on screen; parallel to the activity's onStop().
- onDestroyView(): Called when the fragment's view is no longer needed, to clean up the resources associated with that view.

## desert-clicker app

Handler is a class meant to proccess a queue of messages (known as [android.os.Message]s)
or actions (known as [Runnable]s)

start timer

    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable


    runnable = Runnable {
        secondsCount++
        Timber.i("Timer is at: $secondsCount")

        // postDelayed re-adds the action to the queue of actions the Handler is cycling
        // through the delayMillis param tells the handler to run the runnable in 1 second

        handler.postDelayed(runnable, 1000)
    }

    // this what initially start the timer
    handler.postDelayed(runnable, 1000)

    // Note that the Thread the handler runs on is determined by a class called Looper.

stop the timer

        handler.removeCallbacks(runnable)

calling the timer fun

    dessertTimer = DessertTimer()



       dessertTimer.startTimer()
    
       Timber.i("onStart called")
       
      dessertTimer.stopTimer()
   
      Timber.i("onStop Called")
      
      
- Compile and run the app. In Android Studio, click the Logcat pane. In the Logcat search box, enter dessertclicker, which will filter by both the MainActivity and DessertTimer classes. Notice that once the app starts, the timer also starts running immediately.
- Click the Back button and notice that the timer stops again. The timer stops because both the activity and the timer it controls have been destroyed.
- Use the recents screen to return to the app. Notice in Logcat that the timer restarts from 0.
- Click the Share button. Notice in Logcat that the timer is still running

- Click the Home button. Notice in Logcat the timer stops running.
- Use the recents screen to return to the app. Notice in Logcat the timer starts up again from where it left off because we called startTimer() in the onStart() method.
- In MainActivity, in the onStop() method, comment out the call to stopTimer(). Commenting out stopTimer() demonstrates the case where you start an operation in onStart(), but forget to stop it again in onStop().
- Compile and run the app, and click the Home button after the timer starts. Even though the app is in the background, the timer is running, and continually using system resources. Having the timer continue may unnecessarily use computing resources on your phone, and probably not the behavior you want.

- Uncomment the line in onStop() where you stop the timer.
- Cut and paste the startTimer() call from onStart() to onCreate(). This change demonstrates the case where you both initialize and start a resource in onCreate(), rather than using onCreate() to initialize it and onStart() to start it.
- Compile and run the app. Notice that the timer starts running, as you would expect.
- Click Home to stop the app. The timer stops running, as you would expect.
- Use the recents screen to return to the app. Notice that the timer does not start again in this case, because onCreate() is only called when the app starts—it's not called when an app returns to the foreground.

In the DessertClicker app, it's fairly easy to see that if you started the timer in onStart(), then you need to stop the timer in onStop(). There's only one timer, so stopping the timer is not difficult to remember.

In a more complex Android app, you might set up many things in onStart() or onCreate(), then tear them all down in onStop() or onDestroy(). For example, you might have animations, music, sensors, or timers that you need to both set up and tear down, and start and stop. If you forget one, that leads to bugs and headaches.

The lifecycle library, which is part of Android Jetpack, simplifies this task. The library is especially useful in cases where you have to track many moving parts, some of which are at different lifecycle states. The library flips around the way lifecycles work: Usually the activity or fragment tells a component (such as DessertTimer) what to do when a lifecycle callback occurs. But when you use the lifecycle library, the component itself watches for lifecycle changes, then does what's needed when those changes happen.

There are three main parts of the lifecycle library:

## Complex lifecycle

- Lifecycle owners, which are the components that have (and owns a lifecyle). activity and fragment are lifecycle owners, lifecycle ownser implement the `LifecycleOwner` interface.
- the `Lifecycle` class, which holds the actual state of a lifecycle owner and triggers events when lifecycle changes happen
- lfecycle observers, which observe the lifecycle state and perform tasks when the lifecycle changes. lifecycle observers implement the `LifecycleObserver` interface.

### Turn DessertTimer into a LifecycleObserver
