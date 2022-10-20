@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.yiiguxing.plugin.translate.util

import cn.yiiguxing.plugin.translate.message
import com.intellij.ide.BrowserUtil
import com.intellij.ide.util.PropertiesComponent
import com.intellij.notification.*
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import javax.swing.event.HyperlinkEvent

object Notifications {

    const val DEFAULT_NOTIFICATION_GROUP_ID = "Translation Plugin"

    private const val DO_NOT_SHOW_AGAIN_KEY_PREFIX = "translation.notification.do.not.show.again"

    fun showErrorNotification(
        project: Project?,
        title: String,
        content: String,
        vararg actions: AnAction
    ) {
        showErrorNotification(DEFAULT_NOTIFICATION_GROUP_ID, project, title, content, actions = actions)
    }

    fun showErrorNotification(
        groupId: String,
        project: Project?,
        title: String,
        content: String,
        vararg actions: AnAction
    ) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(groupId)
            .createNotification(content, NotificationType.ERROR)
            .setTitle(title)
            // actions的折叠是从左往右折叠的
            .apply { addActions(actions.toList()) }
            .show(project)
    }

    fun showNotification(
        title: String,
        message: String,
        type: NotificationType,
        project: Project? = null,
        groupId: String = DEFAULT_NOTIFICATION_GROUP_ID,
        notificationCustomizer: (Notification) -> Unit = { }
    ) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(groupId)
            .createNotification(message, type)
            .setTitle(title)
            .apply { notificationCustomizer(this) }
            .show(project)
    }

    fun showInfoNotification(
        title: String,
        message: String,
        project: Project? = null,
        groupId: String = DEFAULT_NOTIFICATION_GROUP_ID
    ) {
        showNotification(title, message, NotificationType.INFORMATION, project, groupId)
    }

    fun showWarningNotification(
        title: String,
        message: String,
        project: Project? = null,
        groupId: String = DEFAULT_NOTIFICATION_GROUP_ID
    ) {
        showNotification(title, message, NotificationType.WARNING, project, groupId)
    }

    fun showErrorNotification(
        title: String,
        message: String,
        project: Project? = null,
        groupId: String = DEFAULT_NOTIFICATION_GROUP_ID
    ) {
        showNotification(title, message, NotificationType.ERROR, project, groupId = groupId)
    }

    fun isDoNotShowAgain(key: String): Boolean {
        return PropertiesComponent.getInstance().getBoolean("$DO_NOT_SHOW_AGAIN_KEY_PREFIX.$key", false)
    }

    fun setDoNotShowAgain(key: String, value: Boolean) {
        PropertiesComponent.getInstance().setValue("$DO_NOT_SHOW_AGAIN_KEY_PREFIX.$key", value)
    }

    open class UrlOpeningListener(expireNotification: Boolean = true) :
        NotificationListener.UrlOpeningListener(expireNotification) {

        override fun hyperlinkActivated(notification: Notification, hyperlinkEvent: HyperlinkEvent) {
            if (!Hyperlinks.handleDefaultHyperlinkActivated(hyperlinkEvent)) {
                super.hyperlinkActivated(notification, hyperlinkEvent)
            }
        }
    }

    class BrowseUrlAction(
        text: String?,
        private val url: String,
        private val expireNotification: Boolean = true
    ) : NotificationAction(text) {

        override fun actionPerformed(e: AnActionEvent, notification: Notification) {
            if (expireNotification) {
                notification.expire()
            }
            BrowserUtil.browse(url)
        }
    }

    class DoNotShowAgainAction(private val key: String) :
        NotificationAction(message("notification.do.not.show.again")) {

        override fun actionPerformed(e: AnActionEvent, notification: Notification) {
            notification.expire()
            setDoNotShowAgain(key, true)
        }
    }
}