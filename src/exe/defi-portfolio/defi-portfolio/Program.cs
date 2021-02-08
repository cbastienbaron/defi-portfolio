using System;
using System.Diagnostics;

namespace defi_portfolio
{
    class Program
    {
        static void Main(string[] args)
        {
            try
            {
                String path = "/k java -jar defi-portfolio_jar.jar";
                Process p = new Process();
                p.StartInfo.CreateNoWindow = true;
                p.StartInfo.Arguments = path;
                p.StartInfo.UseShellExecute = false;
                p.StartInfo.WindowStyle = System.Diagnostics.ProcessWindowStyle.Hidden;
                p.StartInfo.FileName = "cmd.exe";
                p.Start();
            }
            catch (Exception ex)
            {
                Console.WriteLine("Error: {0}", ex.Message);
            }
        }
    }
}
