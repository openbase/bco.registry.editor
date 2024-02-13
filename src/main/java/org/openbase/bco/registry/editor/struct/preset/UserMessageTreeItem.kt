/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2024 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
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
