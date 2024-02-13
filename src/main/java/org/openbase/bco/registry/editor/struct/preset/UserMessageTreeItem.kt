package org.openbase.bco.registry.editor.struct.preset

import com.google.protobuf.Descriptors
import org.openbase.bco.registry.editor.struct.RegistryMessageTreeItem
import org.openbase.bco.registry.remote.Registries
import org.openbase.jul.exception.NotAvailableException
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor
import org.openbase.jul.extension.type.processing.LabelProcessor
import org.openbase.type.domotic.communication.UserMessageType.UserMessage
import org.openbase.type.language.LabelType

/**
 * @author [Tamino Huxohl](mailto:pleminoq@openbase.org)
 */
class UserMessageTreeItem(
    fieldDescriptor: Descriptors.FieldDescriptor?,
    builder: UserMessage.Builder,
    editable: Boolean?,
) : RegistryMessageTreeItem<UserMessage.Builder>(
    fieldDescriptor!!, builder, editable, {
        "${builder.messageType.takeIf { it != UserMessage.MessageType.UNKNOWN } ?: "Message"}${
            try {
                Registries.getUnitRegistry().getUnitConfigById(builder.senderId)
                    .let { LabelProcessor.getBestMatch(it.label, "") }
            } catch (e: NotAvailableException) {
                builder.senderId
            }?.trim().takeIf { !it.isNullOrBlank() }?.let { " from $it" } ?: ""
        } ${
            try {
                Registries.getUnitRegistry().getUnitConfigById(builder.recipientId)
                    .let { LabelProcessor.getBestMatch(it.label, "") }
            } catch (e: NotAvailableException) {
                builder.recipientId
            }?.trim().takeIf { !it.isNullOrBlank() }?.let { " to $it" } ?: ""
        }"
    }
)
