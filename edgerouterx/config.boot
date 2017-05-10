firewall {
    all-ping enable
    broadcast-ping disable
    group {
        ipv6-network-group Nets6-BlackList {
            description "Blacklisted IPv6 Sources"
            ipv6-network 2601:184:4180:69c1::0/64
        }
        network-group Nets4-BlackList {
            description "Blacklisted IPv4 Sources"
            network 192.168.1.0/24
        }
    }
    ipv6-name WANv6_IN {
        default-action drop
        description "WAN inbound traffic forwarded to LAN"
        enable-default-log
        rule 10 {
            action accept
            description "Allow established/related sessions"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
        rule 21 {
            action drop
            description "Drop Nets6Blacklist"
            log disable
            protocol all
            source {
                group {
                    ipv6-network-group Nets6-BlackList
                }
            }
        }
    }
    ipv6-name WANv6_LOCAL {
        default-action drop
        description "WAN inbound traffic to the router"
        enable-default-log
        rule 10 {
            action accept
            description "Allow established/related sessions"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
        rule 21 {
            action drop
            description DropNets6Blacklist
            log disable
            protocol all
            source {
                group {
                    ipv6-network-group Nets6-BlackList
                }
            }
        }
        rule 30 {
            action accept
            description "Allow IPv6 icmp"
            protocol ipv6-icmp
        }
        rule 40 {
            action accept
            description "allow dhcpv6"
            destination {
                port 546
            }
            protocol udp
            source {
                port 547
            }
        }
    }
    ipv6-receive-redirects disable
    ipv6-src-route disable
    ip-src-route disable
    log-martians enable
    name WAN_IN {
        default-action drop
        description "WAN to internal"
        rule 10 {
            action accept
            description "Allow established/related"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
        rule 21 {
            action drop
            description "drop Nets4-Blacklist"
            log disable
            protocol all
            source {
                group {
                    network-group Nets4-BlackList
                }
            }
        }
    }
    name WAN_LOCAL {
        default-action drop
        description "WAN to router"
        rule 10 {
            action accept
            description "Allow established/related"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
        rule 21 {
            action drop
            description "Drop Nets4Blacklist"
            log disable
            protocol all
            source {
                group {
                    network-group Nets4-BlackList
                }
            }
        }
    }
    receive-redirects disable
    send-redirects enable
    source-validation disable
    syn-cookies enable
}
interfaces {
    ethernet eth0 {
        address dhcp
        description Internet
        dhcpv6-pd {
            pd 0 {
                interface switch0 {
                    host-address ::1
                    no-dns
                    prefix-id :1
                    service slaac
                }
                prefix-length /60
            }
            rapid-commit enable
        }
        duplex auto
        firewall {
            in {
                ipv6-name WANv6_IN
                name WAN_IN
            }
            local {
                ipv6-name WANv6_LOCAL
                name WAN_LOCAL
            }
        }
        ipv6 {
            dup-addr-detect-transmits 1
            router-advert {
                cur-hop-limit 64
                link-mtu 0
                managed-flag false
                max-interval 600
                other-config-flag false
                reachable-time 0
                retrans-timer 0
                send-advert true
            }
        }
        poe {
            output off
        }
        speed auto
    }
    ethernet eth1 {
        description Local
        duplex auto
        poe {
            output 24v
        }
        speed auto
    }
    ethernet eth2 {
        description Local
        duplex auto
        speed auto
    }
    ethernet eth3 {
        description Local
        duplex auto
        speed auto
    }
    ethernet eth4 {
        description Local
        duplex auto
        speed auto
    }
    ethernet eth5 {
        duplex auto
        speed auto
    }
    loopback lo {
    }
    switch switch0 {
        address 192.168.1.1/24
        description Local
        ipv6 {
            dup-addr-detect-transmits 1
            router-advert {
                cur-hop-limit 64
                link-mtu 0
                managed-flag true
                max-interval 600
                name-server 2601:184:4180:69c1::1
                other-config-flag false
                prefix ::/64 {
                    autonomous-flag true
                    on-link-flag true
                    valid-lifetime 2592000
                }
                reachable-time 0
                retrans-timer 0
                send-advert true
            }
        }
        mtu 1500
        switch-port {
            interface eth1 {
            }
            interface eth2 {
            }
            interface eth3 {
            }
            interface eth4 {
            }
            vlan-aware disable
        }
    }
}
service {
    dhcp-server {
        disabled false
        hostfile-update disable
        shared-network-name LAN {
            authoritative enable
            subnet 192.168.1.0/24 {
                default-router 192.168.1.1
                dns-server 192.168.1.1
                lease 86400
                start 192.168.1.38 {
                    stop 192.168.1.243
                }
                static-mapping macmini {
                    ip-address 192.168.1.3
                    mac-address 
                }
                static-mapping raspberrypi {
                    ip-address 192.168.1.2
                    mac-address 
                }
                unifi-controller 192.168.1.3
            }
        }
        use-dnsmasq enable
    }
    dns {
        forwarding {
            cache-size 150
            listen-on switch0
            name-server 192.168.1.2
            name-server 2601:184:4180:69c1:fa4c:4c0d:4954:a42c
            options localise-queries
        }
    }
    gui {
        http-port 80
        https-port 443
        older-ciphers enable
    }
    nat {
        rule 5010 {
            description "masquerade for WAN"
            outbound-interface eth0
            type masquerade
        }
    }
    ssh {
        port 22
        protocol-version v2
    }
}
system {
    host-name ubnt
    login {
        user ****** {
            authentication {

            }
            full-name 
            level admin
        }
    }
    name-server 127.0.0.1
    ntp {
        server 0.ubnt.pool.ntp.org {
        }
        server 1.ubnt.pool.ntp.org {
        }
        server 2.ubnt.pool.ntp.org {
        }
        server 3.ubnt.pool.ntp.org {
        }
    }
    offload {
        hwnat disable
        ipsec enable
    }
    syslog {
        global {
            facility all {
                level notice
            }
            facility protocols {
                level debug
            }
        }
    }
    task-scheduler {
        task Update-Blacklists {
            executable {
                path /config/scripts/updBlackList.sh
            }
            interval 12h
        }
    }
    time-zone America/New_York
    traffic-analysis {
        dpi enable
        export enable
    }
}


/* Warning: Do not remove the following line. */
/* === vyatta-config-version: "config-management@1:conntrack@1:cron@1:dhcp-relay@1:dhcp-server@4:firewall@5:ipsec@5:nat@3:qos@1:quagga@2:system@4:ubnt-pptp@1:ubnt-util@1:vrrp@1:webgui@1:webproxy@1:zone-policy@1" === */
/* Release version: v1.9.1.4939092.161214.0702 */
