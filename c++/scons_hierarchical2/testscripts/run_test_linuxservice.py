#!/bin/python

import unittest
import os
import random

class TestLinuxServiceRPM(unittest.TestCase):

    def setUp(self):
        print(' ### setUp ENTER ###')
        if os.geteuid() != 0:
            exit("You need to have root privileges to run this script.")
        print('Current working directory=%s' % os.getcwd())
        lastdir = os.path.basename(os.path.normpath(os.getcwd()))
        if lastdir != 'scons_hierarchical2':
            exit("Test must be started in directory=scons_hierarchical2")
        print(' ### setUp LEAVE ###')

    def tearDown(self):
        print(' ### tearDown ENTER ###')
        print(' ### tearDown LEAVE###')

    def test_installServiceViaRpm_serviceAvailable(self):
        print(' ### tearDown ENTER ###')
        try:
            # preparation
            #
            nameservice = 'linuxservice'
            returnCode = os.system('yum -y remove %s' % nameservice)
            self.assertEqual(returnCode, 0)
            #
            os.system('scons -c')
            sconsTarget = 'linuxservicerpm'
            returnCode = os.system('scons %s' % sconsTarget)
            self.assertEqual(returnCode, 0)
            #
            namerpm = 'rpms/linuxservicerpm/target/debug/source/RPMS/x86_64/linuxservice-0.1-0.x86_64.rpm'
            self.assertTrue(os.path.exists(namerpm))
            #
            returnCode = os.system('yum -y install %s' % namerpm)
            self.assertEqual(returnCode, 0)
            #
            # test
            returnCode = os.system('wget -o - http://localhost:8181')
            self.assertEqual(returnCode, 0)
        finally:
            # clean up
            os.system('scons -c')
            os.system('yum -y remove %s' % nameservice)

if __name__ == '__main__':
    unittest.main()

#