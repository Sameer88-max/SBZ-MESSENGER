
package com.sbz.messenger

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.*
import android.text.InputType
import android.text.TextWatcher
import android.text.Editable

class MainActivity : Activity() {

    private val green = Color.rgb(7, 94, 84)
    private val accent = Color.rgb(18, 140, 126)
    private val pale = Color.rgb(236, 248, 245)
    private val ink = Color.rgb(28, 38, 36)
    private val muted = Color.rgb(112, 126, 122)

    private lateinit var root: LinearLayout
    private lateinit var content: LinearLayout
    private lateinit var bottom: LinearLayout

    private var currentTab = "Chats"

    private val chats = mutableListOf(
        Chat("Ayesha Khan", "Assalam o Alaikum", "10:42", 2),
        Chat("Family Group", "See you soon", "09:18", 0),
        Chat("Ali Raza", "Voice message", "Yesterday", 0),
        Chat("SBZ Support", "Welcome to SBZ Messenger", "Mon", 1)
    )

    private val messages = mutableMapOf<String, MutableList<String>>()

    data class Chat(
        val name: String,
        var last: String,
        var time: String,
        var unread: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showWelcome()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun background(
        color: Int,
        radius: Int = 12
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun makeText(
        value: String,
        size: Float = 15f,
        color: Int = ink,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER_VERTICAL
        }
    }

    private fun makeButton(
        value: String,
        action: () -> Unit
    ): TextView {
        return makeText(value, 15f, Color.WHITE, true).apply {
            gravity = Gravity.CENTER
            setPadding(dp(18), dp(12), dp(18), dp(12))
            background = background(accent, 24)
            setOnClickListener { action() }
        }
    }

    private fun makeField(
        hintText: String,
        type: Int = InputType.TYPE_CLASS_TEXT
    ): EditText {
        return EditText(this).apply {
            hint = hintText
            inputType = type
            textSize = 15f
            setPadding(dp(14), dp(10), dp(14), dp(10))
            background = background(Color.rgb(245, 248, 247))
        }
    }

    private fun newPage(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
        }
    }

    private fun showWelcome() {
        root = newPage()
        root.gravity = Gravity.CENTER
        root.setPadding(dp(26), dp(24), dp(26), dp(24))

        root.addView(
            makeText("SBZ", 42f, green, true).apply {
                gravity = Gravity.CENTER
            }
        )

        root.addView(
            makeText("MESSENGER", 20f, ink, true).apply {
                gravity = Gravity.CENTER
            }
        )

        root.addView(
            makeText("Simple • Secure • Connected", 14f, muted).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(8), 0, dp(28))
            }
        )

        root.addView(
            makeButton("Get Started") {
                showLogin()
            }
        )

        setContentView(root)
    }

    private fun showLogin() {
        root = newPage()
        root.setPadding(dp(24), dp(32), dp(24), dp(24))

        root.addView(makeText("Welcome to SBZ", 28f, green, true))
        root.addView(makeText("Sign in to continue", 15f, muted))

        val name = makeField("Your name")
        val phone = makeField(
            "Mobile number",
            InputType.TYPE_CLASS_PHONE
        )
        val otp = makeField(
            "Demo OTP: 123456",
            InputType.TYPE_CLASS_NUMBER
        )

        listOf(name, phone, otp).forEach {
            val params = LinearLayout.LayoutParams(
                -1,
                dp(56)
            )
            params.topMargin = dp(14)
            root.addView(it, params)
        }

        root.addView(
            makeButton("Verify & Continue") {
                if (name.text.isNullOrBlank() ||
                    phone.text.isNullOrBlank()
                ) {
                    toast("Enter name and phone number")
                } else if (otp.text.toString() != "123456") {
                    toast("Demo OTP is 123456")
                } else {
                    showHome()
                }
            }
        )

        setContentView(root)
    }

    private fun showHome() {
        root = newPage()

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(10), dp(16), dp(8))
            setBackgroundColor(green)
        }

        val top = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
        }

        top.addView(
            makeText("SBZ Messenger", 22f, Color.WHITE, true),
            LinearLayout.LayoutParams(0, dp(45), 1f)
        )

        top.addView(
            makeText("⌕", 28f, Color.WHITE).apply {
                setOnClickListener { showSearch() }
            }
        )

        top.addView(
            makeText("⋮", 26f, Color.WHITE).apply {
                setPadding(dp(12), 0, 0, 0)
                setOnClickListener { showSettings() }
            }
        )

        header.addView(top)

        val tabs = LinearLayout(this).apply {
            gravity = Gravity.CENTER
        }

        listOf("Chats", "Calls", "Groups", "Status").forEach { tab ->

            val item = makeText(
                tab,
                14f,
                Color.WHITE,
                tab == currentTab
            )

            item.gravity = Gravity.CENTER
            item.setPadding(dp(8), dp(10), dp(8), dp(10))

            item.setOnClickListener {
                currentTab = tab
                renderTab()
            }

            tabs.addView(
                item,
                LinearLayout.LayoutParams(0, dp(42), 1f)
            )
        }

        header.addView(tabs)
        root.addView(header)

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(10), dp(12), dp(8))
        }

        root.addView(
            content,
            LinearLayout.LayoutParams(-1, 0, 1f)
        )

        bottom = LinearLayout(this).apply {
            gravity = Gravity.CENTER
            setPadding(dp(4), dp(8), dp(4), dp(8))
        }

        listOf(
            "Chats",
            "Calls",
            "Groups",
            "Status",
            "Settings"
        ).forEach { tab ->

            val item = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
            }

            val icon = makeText(
                iconFor(tab),
                22f,
                if (tab == currentTab) accent else muted
            )

            icon.gravity = Gravity.CENTER

            val label = makeText(
                tab,
                10f,
                if (tab == currentTab) accent else muted
            )

            label.gravity = Gravity.CENTER

            item.addView(icon)
            item.addView(label)

            item.setOnClickListener {
                currentTab = tab
                renderTab()
            }

            bottom.addView(
                item,
                LinearLayout.LayoutParams(0, dp(55), 1f)
            )
        }

        root.addView(bottom)
        setContentView(root)

        renderTab()
    }

    private fun iconFor(tab: String): String {
        return when (tab) {
            "Chats" -> "▤"
            "Calls" -> "☎"
            "Groups" -> "♧"
            "Status" -> "◉"
            else -> "⚙"
        }
    }

    private fun renderTab() {
        content.removeAllViews()

        when (currentTab) {
            "Chats" -> renderChats()
            "Calls" -> renderCalls()
            "Groups" -> renderGroups()
            "Status" -> renderStatus()
            "Settings" -> renderSettings()
        }

        if (::bottom.isInitialized) {
            for (i in 0 until bottom.childCount) {
                val item = bottom.getChildAt(i) as LinearLayout
                val label = item.getChildAt(1) as TextView

                val color =
                    if (label.text.toString() == currentTab)
                        accent else muted

                (item.getChildAt(0) as TextView)
                    .setTextColor(color)

                label.setTextColor(color)
            }
        }
    }

    private fun renderChats() {
        val search = makeField("Search chats")
        search.setSingleLine()

        content.addView(
            search,
            LinearLayout.LayoutParams(-1, dp(48))
        )

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                for (i in content.childCount - 1 downTo 1) {
                    content.removeViewAt(i)
                }

                chats.filter {
                    it.name.contains(s.toString(), true)
                }.forEach {
                    addChatRow(it)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        chats.forEach { addChatRow(it) }

        content.addView(
            makeButton("+ New Chat") {
                showContacts()
            }
        )
    }

    private fun addChatRow(chat: Chat) {
        val row = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), dp(12), dp(8), dp(12))
            setOnClickListener {
                openChat(chat.name)
            }
        }

        val avatar = makeText(
            chat.name.take(1).uppercase(),
            20f,
            Color.WHITE,
            true
        ).apply {
            gravity = Gravity.CENTER
            background = background(accent, 30)
        }

        row.addView(
            avatar,
            LinearLayout.LayoutParams(dp(48), dp(48))
        )

        val middle = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }

        middle.addView(
            makeText(chat.name, 16f, ink, true)
        )

        middle.addView(
            makeText(chat.last, 13f, muted)
        )

        row.addView(
            middle,
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        val time = makeText(chat.time, 11f, muted)
        row.addView(time)

        content.addView(row)

        content.addView(
            View(this).apply {
                setBackgroundColor(Color.LTGRAY)
            },
            LinearLayout.LayoutParams(-1, dp(1))
        )
    }

    private fun openChat(name: String) {
        val list = messages.getOrPut(name) {
            mutableListOf(
                "Assalam o Alaikum",
                "Welcome to SBZ Messenger!"
            )
        }

        val page = newPage()

        val bar = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(8), dp(12), dp(8))
            setBackgroundColor(green)
        }

        bar.addView(
            makeText("‹", 32f, Color.WHITE).apply {
                setOnClickListener { showHome() }
            }
        )

        bar.addView(
            makeText(name, 18f, Color.WHITE, true),
            LinearLayout.LayoutParams(0, dp(48), 1f)
        )

        bar.addView(
            makeText("☎", 22f, Color.WHITE).apply {
                setOnClickListener {
                    toast("Real audio calling requires calling service")
                }
            }
        )

        bar.addView(
            makeText("▣", 22f, Color.WHITE).apply {
                setPadding(dp(12), 0, 0, 0)
                setOnClickListener {
                    toast("Real video calling requires calling service")
                }
            }
        )

        page.addView(bar)

        val scroll = ScrollView(this)

        val messageList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }

        fun refreshMessages() {
            messageList.removeAllViews()

            list.forEachIndexed { index, message ->

                val bubble = makeText(message, 15f, ink)

                bubble.setPadding(
                    dp(12), dp(9), dp(12), dp(9)
                )

                bubble.background = background(
                    if (index % 2 == 0) pale
                    else Color.rgb(239, 243, 242),
                    14
                )

                val wrapper = LinearLayout(this).apply {
                    gravity =
                        if (index % 2 == 0) Gravity.START
                        else Gravity.END
                }

                wrapper.addView(bubble)

                messageList.addView(
                    wrapper,
                    LinearLayout.LayoutParams(-1, -2).apply {
                        bottomMargin = dp(8)
                    }
                )
            }
        }

        refreshMessages()
        scroll.addView(messageList)

        page.addView(
            scroll,
            LinearLayout.LayoutParams(-1, 0, 1f)
        )

        val composer = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), dp(6), dp(8), dp(6))
        }

        val input = makeField("Message")

        composer.addView(
            makeText("＋", 26f, accent).apply {
                setPadding(dp(8), 0, dp(8), 0)
                setOnClickListener {
                    showAttachmentOptions()
                }
            }
        )

        composer.addView(
            input,
            LinearLayout.LayoutParams(0, dp(48), 1f)
        )

        composer.addView(
            makeText("➤", 24f, accent, true).apply {
                setPadding(dp(12), 0, dp(8), 0)

                setOnClickListener {
                    val value = input.text.toString().trim()

                    if (value.isNotEmpty()) {
                        list.add(value)

                        chats.find {
                            it.name == name
                        }?.apply {
                            last = value
                            time = "Now"
                        }

                        input.setText("")
                        refreshMessages()
                        scroll.post {
                            scroll.fullScroll(View.FOCUS_DOWN)
                        }
                    }
                }
            }
        )

        page.addView(composer)
        setContentView(page)
    }

    private fun renderCalls() {
        content.addView(makeText("Recent Calls", 22f, ink, true))

        listOf("Ayesha Khan", "Ali Raza", "SBZ Support")
            .forEach {
                actionRow(it, "Audio / Video call") {
                    toast("Calling SDK integration required")
                }
            }

        content.addView(
            makeButton("+ New Call") {
                showContacts()
            }
        )
    }

    private fun renderGroups() {
        content.addView(makeText("Your Groups", 22f, ink, true))

        actionRow("Family Group", "12 Members") {
            openChat("Family Group")
        }

        actionRow("Friends Circle", "8 Members") {
            openChat("Friends Circle")
        }

        content.addView(
            makeButton("+ Create Group") {
                createGroup()
            }
        )
    }

    private fun renderStatus() {
        content.addView(makeText("Status Updates", 22f, ink, true))

        actionRow("+ My Status", "Share a photo or text update") {
            showAttachmentOptions()
        }

        listOf("Ayesha Khan", "Ali Raza", "Friends Circle")
            .forEach {
                actionRow(it, "Recent update") {
                    toast("Status viewer needs media integration")
                }
            }
    }

    private fun renderSettings() {
        content.addView(makeText("Settings", 24f, ink, true))

        actionRow("Profile", "Name, photo, About and SBZ ID") {
            showProfile()
        }

        actionRow("Privacy", "Last seen and read receipts") {
            showPrivacy()
        }

        actionRow("Notifications", "Message and call alerts") {
            toast("Notification setup required")
        }

        actionRow("Appearance", "Themes and chat wallpaper") {
            toast("Theme settings")
        }

        actionRow("Storage and Data", "Media and network usage") {
            toast("Storage manager")
        }

        actionRow("Help", "Help centre and support") {
            toast("SBZ Support")
        }

        actionRow("About", "SBZ Messenger Version 1.0") {
            toast("SBZ Messenger 1.0")
        }

        content.addView(
            makeButton("Sign Out") {
                showWelcome()
            }
        )
    }

    private fun actionRow(
        title: String,
        subtitle: String,
        action: () -> Unit
    ) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(14), dp(12), dp(14))
            setOnClickListener { action() }
        }

        row.addView(makeText(title, 16f, ink, true))
        row.addView(makeText(subtitle, 12f, muted))

        content.addView(row)

        content.addView(
            View(this).apply {
                setBackgroundColor(Color.LTGRAY)
            },
            LinearLayout.LayoutParams(-1, dp(1))
        )
    }

    private fun showProfile() {
        val page = newPage()
        page.setPadding(dp(20), dp(20), dp(20), dp(20))

        page.addView(
            makeText("‹  Profile", 22f, green, true).apply {
                setOnClickListener { showHome() }
            }
        )

        page.addView(
            makeText("SBZ ID: SBZ100001", 15f, accent, true)
        )

        val name = makeField("Display Name")
        val about = makeField("About")

        page.addView(name)
        page.addView(about)

        page.addView(
            makeButton("Save Profile") {
                toast("Demo profile saved")
            }
        )

        setContentView(page)
    }

    private fun showPrivacy() {
        val page = newPage()
        page.setPadding(dp(20), dp(20), dp(20), dp(20))

        page.addView(
            makeText("‹  Privacy Settings", 22f, green, true).apply {
                setOnClickListener { showHome() }
            }
        )

        listOf(
            "Last seen and online",
            "Profile photo visibility",
            "About visibility",
            "Read receipts",
            "Blocked contacts"
        ).forEach {
            page.addView(
                CheckBox(this).apply {
                    text = it
                    isChecked = true
                }
            )
        }

        page.addView(
            makeButton("Save Settings") {
                toast("Demo privacy settings saved")
            }
        )

        setContentView(page)
    }

    private fun showSearch() {
        val page = newPage()
        page.setPadding(dp(18), dp(18), dp(18), dp(18))

        page.addView(
            makeText("‹  Search", 22f, green, true).apply {
                setOnClickListener { showHome() }
            }
        )

        page.addView(makeField("Search contacts and messages"))

        setContentView(page)
    }

    private fun showContacts() {
        val page = newPage()
        page.setPadding(dp(18), dp(18), dp(18), dp(18))

        page.addView(
            makeText("‹  Contacts", 22f, green, true).apply {
                setOnClickListener { showHome() }
            }
        )

        listOf("Ayesha Khan", "Ali Raza", "SBZ Support")
            .forEach { name ->

                page.addView(
                    makeText("●   $name", 17f, ink, true).apply {
                        setPadding(0, dp(16), 0, dp(16))
                        setOnClickListener {
                            openChat(name)
                        }
                    }
                )
            }

        setContentView(page)
    }

    private fun createGroup() {
        val page = newPage()
        page.setPadding(dp(18), dp(18), dp(18), dp(18))

        page.addView(
            makeText("‹  Create Group", 22f, green, true).apply {
                setOnClickListener { showHome() }
            }
        )

        val groupName = makeField("Group Name")
        page.addView(groupName)

        page.addView(
            makeButton("Create Group") {
                val name = groupName.text.toString().trim()

                if (name.isBlank()) {
                    toast("Enter group name")
                } else {
                    chats.add(
                        0,
                        Chat(name, "Group created", "Now", 0)
                    )
                    currentTab = "Groups"
                    showHome()
                }
            }
        )

        setContentView(page)
    }

    private fun showAttachmentOptions() {
        val options = arrayOf(
            "Gallery",
            "Camera",
            "Document",
            "Location",
            "Contact"
        )

        android.app.AlertDialog.Builder(this)
            .setTitle("Attach")
            .setItems(options) { _, which ->
                toast(
                    when (which) {
                        0 -> "Gallery integration required"
                        1 -> "Camera integration required"
                        2 -> "Document picker required"
                        3 -> "Location permission required"
                        else -> "Contacts permission required"
                    }
                )
            }
            .show()
    }

    private fun showSettings() {
        currentTab = "Settings"
        showHome()
    }

    private fun toast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}
