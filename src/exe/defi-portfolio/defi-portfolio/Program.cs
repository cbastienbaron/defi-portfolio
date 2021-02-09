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
                String path = "/k java --module-path javafx-sdk-11.0.2\\lib --add-modules javafx.controls,javafx.fxml,javafx.media -jar defi-portfolio_jar.jar";
                Process p = new Process();
                p.StartInfo.CreateNoWindow = true;
                p.StartInfo.Arguments = path;
                p.StartInfo.UseShellExecute = false;
                p.StartInfo.WindowStyle = System.Diagnostics.ProcessWindowStyle.Normal;
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
