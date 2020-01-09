/*
 * The MIT License
 *
 * Copyright (c) 2020 aoju.org All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aoju.bus.health.software.windows;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.IPHlpAPI;
import com.sun.jna.platform.win32.IPHlpAPI.FIXED_INFO;
import com.sun.jna.platform.win32.IPHlpAPI.IP_ADDR_STRING;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.ptr.IntByReference;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.Command;
import org.aoju.bus.health.software.AbstractNetwork;
import org.aoju.bus.logger.Logger;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * WindowsNetworkParams class.
 * </p>
 *
 * @author Kimi Liu
 * @version 5.5.2
 * @since JDK 1.8+
 */
public class WindowsNetwork extends AbstractNetwork {

    private static final int COMPUTER_NAME_DNS_DOMAIN_FULLY_QUALIFIED = 3;

    @Override
    public String getDomainName() {
        char[] buffer = new char[256];
        IntByReference bufferSize = new IntByReference(buffer.length);
        if (!Kernel32.INSTANCE.GetComputerNameEx(COMPUTER_NAME_DNS_DOMAIN_FULLY_QUALIFIED, buffer, bufferSize)) {
            Logger.error("Failed to get dns domain name. Error code: {}", Kernel32.INSTANCE.GetLastError());
            return Normal.EMPTY;
        }
        return new String(buffer).trim();
    }

    @Override
    public String[] getDnsServers() {
        IntByReference bufferSize = new IntByReference();
        int ret = IPHlpAPI.INSTANCE.GetNetworkParams(null, bufferSize);
        if (ret != WinError.ERROR_BUFFER_OVERFLOW) {
            Logger.error("Failed to get network parameters buffer size. Error code: {}", ret);
            return Normal.EMPTY_STRING_ARRAY;
        }

        Memory buffer = new Memory(bufferSize.getValue());
        ret = IPHlpAPI.INSTANCE.GetNetworkParams(buffer, bufferSize);
        if (ret != 0) {
            Logger.error("Failed to get network parameters. Error code: {}", ret);
            return Normal.EMPTY_STRING_ARRAY;
        }
        FIXED_INFO fixedInfo = new FIXED_INFO(buffer);

        List<String> list = new ArrayList<>();
        IP_ADDR_STRING dns = fixedInfo.DnsServerList;
        while (dns != null) {
            // a char array of size 16.
            // This array holds an IPv4 address in dotted decimal notation.
            String addr = new String(dns.IpAddress.String, StandardCharsets.US_ASCII);
            int nullPos = addr.indexOf(0);
            if (nullPos != -1) {
                addr = addr.substring(0, nullPos);
            }
            list.add(addr);
            dns = dns.Next;
        }
        return list.toArray(Normal.EMPTY_STRING_ARRAY);
    }

    @Override
    public String getHostName() {
        return Kernel32Util.getComputerName();
    }

    @Override
    public String getIpv4DefaultGateway() {
        return parseIpv4Route();
    }

    @Override
    public String getIpv6DefaultGateway() {
        return parseIpv6Route();
    }

    private String parseIpv4Route() {
        List<String> lines = Command.runNative("route print -4 0.0.0.0");
        for (String line : lines) {
            String[] fields = Builder.whitespaces.split(line.trim());
            if (fields.length > 2 && "0.0.0.0".equals(fields[0])) {
                return fields[2];
            }
        }
        return Normal.EMPTY;
    }

    private String parseIpv6Route() {
        List<String> lines = Command.runNative("route print -6 ::/0");
        for (String line : lines) {
            String[] fields = Builder.whitespaces.split(line.trim());
            if (fields.length > 3 && "::/0".equals(fields[2])) {
                return fields[3];
            }
        }
        return Normal.EMPTY;
    }

}
